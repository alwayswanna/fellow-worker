package a.gleb.oauth2server.scheduler;

import a.gleb.oauth2server.service.OutboxMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OutboxMessageScheduler {

    private final OutboxMessageService outboxMessageService;

    @Scheduled(cron = "${fellow-worker-oauth2-server.send-out-box-message.cron}")
    public void proceed() {
        try {
            log.info("Start scheduled process on send outbox message");
            outboxMessageService.proceed();
            log.info("Finish scheduled process on send outbox message");
        } catch (Exception e) {
            log.error("Error on send outbox messages, ", e);
        }
    }
}
