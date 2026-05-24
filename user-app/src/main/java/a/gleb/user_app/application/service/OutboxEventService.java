package a.gleb.user_app.application.service;

import a.gleb.user_app.application.mapper.UserEventPayloadMapper;
import a.gleb.user_app.application.port.out.OutboxEventRepositoryPort;
import a.gleb.user_app.domain.model.OutboxEvent;
import a.gleb.user_app.domain.model.User;
import a.gleb.fellow_worker.kafka.event.OutboxEventStatus;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private static final long BASE_BACKOFF_SECONDS = 10;
    private static final long MAX_BACKOFF_SECONDS = 600;

    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final UserEventPayloadMapper userEventPayloadMapper;
    private final JsonMapper jsonMapper;
    private final MeterRegistry meterRegistry;

    /**
     * Saves an outbox event within the current transaction.
     * Must be called inside an active transaction - throws if no transaction is present.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(User user, UserEventType eventType) {
        var payload = userEventPayloadMapper.toPayload(user, eventType);
        var json = jsonMapper.writeValueAsString(payload);
        var event = OutboxEvent.builder()
                .aggregateType("USER")
                .aggregateId(user.getId())
                .eventType(eventType)
                .payload(json)
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .nextAttemptAt(LocalDateTime.now())
                .build();
        outboxEventRepositoryPort.save(event);
        log.debug("OutboxEventService: saved outbox event [eventType={}, userId={}]", eventType, user.getId());
    }

    @Transactional(readOnly = true)
    public List<OutboxEvent> findPendingEvents(int batchSize) {
        return outboxEventRepositoryPort.findPending(batchSize);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublished(UUID eventId) {
        outboxEventRepositoryPort.findById(eventId).ifPresent(event -> {
            event.setStatus(OutboxEventStatus.PUBLISHED);
            event.setProcessedAt(LocalDateTime.now());
            event.setErrorMessage(null);
            outboxEventRepositoryPort.save(event);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID eventId, String errorMessage, int maxRetries) {
        outboxEventRepositoryPort.findById(eventId).ifPresent(event -> {
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(errorMessage);
            if (event.getRetryCount() >= maxRetries) {
                event.setStatus(OutboxEventStatus.FAILED);
                event.setProcessedAt(LocalDateTime.now());
                log.warn("OutboxEventService: event exceeded max retries, marked FAILED [id={}, retries={}]",
                        eventId, event.getRetryCount());
                Counter.builder("outbox.events.failed")
                        .tag("aggregateType", event.getAggregateType())
                        .tag("eventType", event.getEventType().name())
                        .register(meterRegistry)
                        .increment();
            } else {
                event.setNextAttemptAt(LocalDateTime.now().plusSeconds(backoffSeconds(event.getRetryCount())));
            }
            outboxEventRepositoryPort.save(event);
        });
    }

    private long backoffSeconds(int retryCount) {
        return Math.min(MAX_BACKOFF_SECONDS, BASE_BACKOFF_SECONDS * (1L << retryCount));
    }
}
