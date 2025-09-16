package a.gleb.main_service.db.repository

import a.gleb.main_service.db.entity.OutboxMessageEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface OutboxMessageEntityRepository : CoroutineCrudRepository<OutboxMessageEntity, UUID> {

    fun findOutboxMessageEntityBySent(sent: Boolean): List<OutboxMessageEntity>
}