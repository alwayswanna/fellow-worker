package a.gleb.fellow_worker.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyEventPayload(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        UUID companyId
) {
}
