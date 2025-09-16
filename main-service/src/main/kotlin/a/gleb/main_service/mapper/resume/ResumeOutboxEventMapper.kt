package a.gleb.main_service.mapper.resume

import a.gleb.apicommon.event.EventType
import a.gleb.apicommon.event.resume.ResumeAdditionalInformation
import a.gleb.apicommon.event.resume.ResumeEvent
import a.gleb.main_service.db.entity.resume.Resume
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class ResumeOutboxEventMapper {

    suspend fun toEvent(entity: Resume, type: EventType): ResumeEvent {
        return ResumeEvent.builder()
            .eventId(UUID.randomUUID())
            .type(type)
            .createdAt(LocalDateTime.now())
            .information(ResumeAdditionalInformation(entity.id))
            .build()
    }
}