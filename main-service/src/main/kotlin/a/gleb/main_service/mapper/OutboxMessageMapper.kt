package a.gleb.main_service.mapper

import a.gleb.main_service.db.entity.OutboxMessageEntity
import org.springframework.stereotype.Component

@Component
class OutboxMessageMapper {

    suspend fun toOutboxMessage(message: String, bindingName: String) : OutboxMessageEntity {
        return OutboxMessageEntity(
            message = message,
            bindingName = bindingName,
            sent = false
        )
    }
}