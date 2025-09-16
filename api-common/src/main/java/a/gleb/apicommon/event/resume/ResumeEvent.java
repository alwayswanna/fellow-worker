package a.gleb.apicommon.event.resume;

import a.gleb.apicommon.event.EventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResumeEvent(
        UUID eventId,
        EventType type,
        LocalDateTime createdAt,
        ResumeAdditionalInformation information
) {
    @Builder
    public ResumeEvent {}
}
