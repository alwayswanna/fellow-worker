package a.gleb.main_service.service

import a.gleb.main_service.db.repository.OutboxMessageEntityRepository
import mu.KotlinLogging
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OutboxMessageService(
    private val outboxMessageRepository: OutboxMessageEntityRepository,
    private val streamBridge: StreamBridge
) {

    /**
     * Method for sending [a.gleb.main_service.db.entity.OutboxMessageEntity] to Kafka topic.
     */
    suspend fun processOutboxMessage() {
        outboxMessageRepository.findOutboxMessageEntityBySent(false)
            .forEach { outboxMessageEntity ->
                logger.info { "Processing outbox message: ${outboxMessageEntity.id}" }
                streamBridge.send(outboxMessageEntity.bindingName, outboxMessageEntity)
                outboxMessageEntity.sent = true
                outboxMessageRepository.save(outboxMessageEntity)
            }
    }
}