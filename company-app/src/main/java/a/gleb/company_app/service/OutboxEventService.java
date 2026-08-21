package a.gleb.company_app.service;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.company_app.db.entity.OutboxEventEntity;
import a.gleb.company_app.db.repository.OutboxEventRepository;
import a.gleb.company_app.mapper.CompanyEventMapper;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import a.gleb.fellow_worker.kafka.event.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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

    private final OutboxEventRepository outboxEventRepository;
    private final CompanyEventMapper companyEventMapper;
    private final JsonMapper jsonMapper;

    /**
     * Saves an outbox event within the current transaction.
     * Must be called inside an active transaction — throws if no transaction is present.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(CompanyEntity company, CompanyEventType eventType) {
        var payload = companyEventMapper.toPayload(company, eventType);
        var json = jsonMapper.writeValueAsString(payload);
        var event = OutboxEventEntity.builder()
                .aggregateType("COMPANY")
                .aggregateId(company.getId())
                .eventType(eventType)
                .payload(json)
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        outboxEventRepository.save(event);
        log.debug("OutboxEventService: saved outbox event [eventType={}, companyId={}]", eventType, company.getId());
    }

    @Transactional(readOnly = true)
    public List<OutboxEventEntity> findPendingEvents(int batchSize) {
        return outboxEventRepository.findPendingEvents(OutboxEventStatus.PENDING, PageRequest.of(0, batchSize));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markPublished(UUID eventId) {
        outboxEventRepository.findById(eventId).ifPresent(event -> {
            event.setStatus(OutboxEventStatus.PUBLISHED);
            event.setProcessedAt(LocalDateTime.now());
            event.setErrorMessage(null);
            outboxEventRepository.save(event);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID eventId, String errorMessage, int maxRetries) {
        outboxEventRepository.findById(eventId).ifPresent(event -> {
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(errorMessage);
            if (event.getRetryCount() >= maxRetries) {
                event.setStatus(OutboxEventStatus.FAILED);
                event.setProcessedAt(LocalDateTime.now());
                log.warn("OutboxEventService: event exceeded max retries, marked FAILED [id={}, retries={}]",
                        eventId, event.getRetryCount());
            }
            outboxEventRepository.save(event);
        });
    }
}
