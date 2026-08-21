package a.gleb.user_app.domain.model;

import a.gleb.fellow_worker.kafka.event.OutboxEventStatus;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private UserEventType eventType;
    private String payload;
    private OutboxEventStatus status;
    private int retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime processedAt;
    private String errorMessage;
}
