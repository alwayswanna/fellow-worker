package a.gleb.user_app.adapter.out.persistence;

import a.gleb.user_app.adapter.out.persistence.entity.OutboxEventEntity;
import a.gleb.user_app.adapter.out.persistence.repository.OutboxEventRepository;
import a.gleb.user_app.application.port.out.OutboxEventRepositoryPort;
import a.gleb.user_app.domain.model.OutboxEvent;
import a.gleb.fellow_worker.kafka.event.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    public void save(OutboxEvent event) {
        outboxEventRepository.save(toEntity(event));
    }

    @Override
    public Optional<OutboxEvent> findById(UUID id) {
        return outboxEventRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<OutboxEvent> findPending(int batchSize) {
        return outboxEventRepository.findPendingEvents(OutboxEventStatus.PENDING, PageRequest.of(0, batchSize))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private OutboxEvent toDomain(OutboxEventEntity entity) {
        return OutboxEvent.builder()
                .id(entity.getId())
                .aggregateType(entity.getAggregateType())
                .aggregateId(entity.getAggregateId())
                .eventType(entity.getEventType())
                .payload(entity.getPayload())
                .status(entity.getStatus())
                .retryCount(entity.getRetryCount())
                .createdAt(entity.getCreatedAt())
                .nextAttemptAt(entity.getNextAttemptAt())
                .processedAt(entity.getProcessedAt())
                .errorMessage(entity.getErrorMessage())
                .build();
    }

    private OutboxEventEntity toEntity(OutboxEvent event) {
        return OutboxEventEntity.builder()
                .id(event.getId())
                .aggregateType(event.getAggregateType())
                .aggregateId(event.getAggregateId())
                .eventType(event.getEventType())
                .payload(event.getPayload())
                .status(event.getStatus())
                .retryCount(event.getRetryCount())
                .createdAt(event.getCreatedAt())
                .nextAttemptAt(event.getNextAttemptAt())
                .processedAt(event.getProcessedAt())
                .errorMessage(event.getErrorMessage())
                .build();
    }
}
