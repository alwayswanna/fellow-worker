package a.gleb.vacancy_app.messaging;

import a.gleb.fellow_worker.kafka.event.CompanyEventPayload;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import a.gleb.vacancy_app.db.repository.VacancyRepository;
import a.gleb.vacancy_app.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Kafka bindings for vacancy-app.
 * Reacts to `user.event` (published by user-app's outbox on account create/update/delete) —
 * applications submitted by a deleted account are hard deleted.
 * Reacts to `company.event` (published by company-app's outbox when a company is deleted, e.g.
 * as a cascade of its owner account being removed) — vacancies of the deleted company are hard
 * deleted; DB-level `ON DELETE CASCADE` takes care of vacancy_skill/application rows.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class VacancyAppKafkaConfiguration {

    private final ApplicationService applicationService;
    private final VacancyRepository vacancyRepository;

    @Bean
    public Consumer<UserEventPayload> userEventConsumer() {
        return this::handleUserEvent;
    }

    @Bean
    public Consumer<CompanyEventPayload> companyEventConsumer() {
        return this::handleCompanyEvent;
    }

    private void handleUserEvent(UserEventPayload payload) {
        UserEventType eventType;
        try {
            eventType = UserEventType.valueOf(payload.eventType());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("VacancyAppKafkaConfiguration: unknown eventType [eventType={}, userId={}]", payload.eventType(), payload.userId());
            return;
        }

        if (eventType == UserEventType.USER_DELETED) {
            applicationService.deleteAllForAccount(payload.userId());
        }
    }

    @Transactional
    public void handleCompanyEvent(CompanyEventPayload payload) {
        CompanyEventType eventType;
        try {
            eventType = CompanyEventType.valueOf(payload.eventType());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("VacancyAppKafkaConfiguration: unknown eventType [eventType={}, companyId={}]", payload.eventType(), payload.companyId());
            return;
        }

        if (eventType != CompanyEventType.COMPANY_DELETED) {
            return;
        }

        vacancyRepository.deleteAllByCompanyId(payload.companyId());
        log.info("VacancyAppKafkaConfiguration: deleted vacancies for removed company [companyId={}]", payload.companyId());
    }
}
