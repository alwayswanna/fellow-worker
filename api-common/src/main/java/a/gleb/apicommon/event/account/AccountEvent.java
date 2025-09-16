package a.gleb.apicommon.event.account;

import a.gleb.apicommon.event.EventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Events for account domain model.
 *
 * @param eventId     identifier of event
 * @param createdAt   date-time when event created
 * @param type        action on domain model
 * @param information information about model
 */
public record AccountEvent(
        UUID eventId,
        EventType type,
        LocalDateTime createdAt,
        AccountAdditionalInformation information

) {
    @Builder
    public AccountEvent {}
}
