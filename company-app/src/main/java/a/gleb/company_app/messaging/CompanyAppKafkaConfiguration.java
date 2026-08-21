package a.gleb.company_app.messaging;

import a.gleb.company_app.service.CompanyRecruiterService;
import a.gleb.company_app.service.CompanyReviewService;
import a.gleb.company_app.service.CompanyService;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Kafka bindings for company-app.
 * Reacts to `user.event` (published by user-app's outbox on account create/update/delete).
 * Only `USER_DELETED` is handled today:
 * - the account's recruiter membership (if any) and reviews are removed;
 * - if the account owns a company, the company is deleted and a `COMPANY_DELETED` outbox event
 *   is emitted so vacancy-app can drop the company's vacancies/applications in turn.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CompanyAppKafkaConfiguration {

    private final CompanyRecruiterService recruiterService;
    private final CompanyReviewService reviewService;
    private final CompanyService companyService;

    @Bean
    public Consumer<UserEventPayload> userEventConsumer() {
        return this::handleUserEvent;
    }

    private void handleUserEvent(UserEventPayload payload) {
        UserEventType eventType;
        try {
            eventType = UserEventType.valueOf(payload.eventType());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("CompanyAppKafkaConfiguration: unknown eventType [eventType={}, userId={}]", payload.eventType(), payload.userId());
            return;
        }

        if (eventType != UserEventType.USER_DELETED) {
            return;
        }

        var accountId = payload.userId();
        recruiterService.deleteByAccountId(accountId);
        reviewService.deleteAllByAccountId(accountId);
        companyService.deleteOwnedByAccount(accountId);

        log.info("CompanyAppKafkaConfiguration: processed USER_DELETED [userId={}]", accountId);
    }
}
