package a.gleb.main_service.scheduler

import a.gleb.main_service.service.OutboxMessageService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class OutboxMessageScheduler(
    private val outboxMessageService: OutboxMessageService,
) {

    @Scheduled(fixedDelay = 60000)
    suspend fun proceedOutboxMessage() {
        logger.info { "Start proceeding outbox message" }
        outboxMessageService.processOutboxMessage()
        logger.info { "Finish proceeding outbox message" }
    }
}