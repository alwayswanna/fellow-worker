package a.gleb.main_service.service.resume

import a.gleb.apicommon.event.EventType
import a.gleb.main_service.constant.RESUME_EVENT_OUT_BINDING_NAME
import a.gleb.main_service.db.entity.OutboxMessageEntity
import a.gleb.main_service.db.entity.resume.Resume
import a.gleb.main_service.db.repository.OutboxMessageEntityRepository
import a.gleb.main_service.mapper.resume.ResumeOutboxEventMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class ResumeOutboxEventService(
    private val objectMapper: ObjectMapper,
    private val resumeOutboxEventMapper: ResumeOutboxEventMapper,
    private val outboxMessageRepository: OutboxMessageEntityRepository
) {

    suspend fun createEvent(entity: Resume, type: EventType) {
        val event = resumeOutboxEventMapper.toEvent(entity, type)
        val eventJsonbValue: String = objectMapper.writeValueAsString(event)

        val entity = OutboxMessageEntity(
            message = eventJsonbValue,
            bindingName = RESUME_EVENT_OUT_BINDING_NAME,
            sent = false
        )

        outboxMessageRepository.save(entity)
    }
}