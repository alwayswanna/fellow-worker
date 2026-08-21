package a.gleb.resume_app.messaging;

import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import a.gleb.resume_app.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Kafka bindings for resume-app.
 * Reacts to `user.event` (published by user-app's outbox on account create/update/delete).
 * Only `USER_DELETED` is handled today — resumes belonging to the deleted account are hard
 * deleted; DB-level `ON DELETE CASCADE` takes care of work experience/education/skill/link rows.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResumeAppKafkaConfiguration {

    private final ResumeService resumeService;

    @Bean
    public Consumer<UserEventPayload> userEventConsumer() {
        return this::handleUserEvent;
    }

    private void handleUserEvent(UserEventPayload payload) {
        UserEventType eventType;
        try {
            eventType = UserEventType.valueOf(payload.eventType());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("ResumeAppKafkaConfiguration: unknown eventType [eventType={}, userId={}]", payload.eventType(), payload.userId());
            return;
        }

        if (eventType == UserEventType.USER_DELETED) {
            resumeService.deleteAllForAccount(payload.userId());
        }
    }
}
