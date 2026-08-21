package a.gleb.company_app.scheduler;

import a.gleb.company_app.config.properties.CompanyAppConfigurationProperties;
import a.gleb.company_app.service.OutboxEventService;
import a.gleb.company_app.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingScheduler {

    private final OutboxEventService outboxEventService;
    private final OutboxPublisherService outboxPublisherService;
    private final CompanyAppConfigurationProperties properties;

    @Scheduled(cron = "${app.outbox.scheduler.cron}")
    @SchedulerLock(
            name = "lockOutboxPolling",
            lockAtLeastFor = "${app.outbox.lock.at-least-for}",
            lockAtMostFor = "${app.outbox.lock.at-most-for}"
    )
    public void pollAndPublish() {
        var events = outboxEventService.findPendingEvents(properties.outbox().batchSize());
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        log.info("OutboxPollingScheduler: processing {} pending events", events.size());

        for (var event : events) {
            try {
                outboxPublisherService.publish(event);
                outboxEventService.markPublished(event.getId());
            } catch (Exception e) {
                log.error("OutboxPollingScheduler: failed to publish event [id={}, eventType={}]: {}",
                        event.getId(), event.getEventType(), e.getMessage(), e);
                outboxEventService.markFailed(event.getId(), e.getMessage(), properties.outbox().maxRetries());
            }
        }
    }
}
