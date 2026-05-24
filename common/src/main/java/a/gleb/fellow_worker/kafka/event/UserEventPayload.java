package a.gleb.fellow_worker.kafka.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserEventPayload(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        UUID userId,
        String login,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String roleCode,
        String roleName,
        LocalDateTime userCreatedAt,
        LocalDateTime userUpdatedAt
) {
}
