/*
 * Copyright (c) 3-3/30/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.scheduler;


import a.gleb.clientmanager.service.AccountEntitiesService;
import a.gleb.clientmanager.service.db.DeletedAccountDatabaseService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeletedAccountScheduledTask {

    /**
     * Metric name for clean failed user entities.
     */
    private static final String SCHEDULED_TASK_STATE_METRIC_NAME = "failure.clean.user.entities.scheduled.task";

    private final DeletedAccountDatabaseService deletedAccountDatabaseService;
    private final AccountEntitiesService accountEntitiesService;
    private final Counter scheduledTaskStateMetric;

    public DeletedAccountScheduledTask(
            DeletedAccountDatabaseService deletedAccountDatabaseService,
            AccountEntitiesService accountEntitiesService,
            MeterRegistry meterRegistry
    ){
        this.deletedAccountDatabaseService = deletedAccountDatabaseService;
        this.accountEntitiesService = accountEntitiesService;
        this.scheduledTaskStateMetric = meterRegistry.counter(SCHEDULED_TASK_STATE_METRIC_NAME);
    }

    /**
     * Cron-job for clean deleted account cache.
     */
    @Scheduled(cron = "${client-manager.deleted-account-clear-task.cron}")
    public void cleanUpDeletedAccountCache() {
        log.info("Start deleted account cleaning scheduled task");
        var cache = deletedAccountDatabaseService.findAllDeletedAccounts();
        log.info("Cache size: {}", cache.size());
        cache.forEach(it -> {
            try {
                accountEntitiesService.remove(it.getId());
                deletedAccountDatabaseService.delete(it);
            } catch (Exception e) {
                /* if API call throws exception increment custom metric */
                scheduledTaskStateMetric.increment();
                log.error("Error while remove user entities.", e);
            }
        });
        log.info("End deleted account cleaning scheduled task");
    }
}
