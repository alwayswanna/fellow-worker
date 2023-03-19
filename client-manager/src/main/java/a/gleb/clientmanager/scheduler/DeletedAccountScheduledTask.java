/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.scheduler;


import a.gleb.clientmanager.service.AccountEntitiesService;
import a.gleb.clientmanager.service.DeletedAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DeletedAccountScheduledTask {

    private final DeletedAccountService deletedAccountService;
    private final AccountEntitiesService accountEntitiesService;

    /**
     * Cron-job for clean deleted account cache.
     */
    @Scheduled(cron = "${client-manager.deleted-account-clear-task.cron}")
    public void cleanUpDeletedAccountCache() {
        log.info("Start deleted account cleaning scheduled task");
        var cache = deletedAccountService.findAllDeletedAccounts();
        log.info("Cache size: {}", cache.size());
        cache.forEach(it -> {
            try {
                accountEntitiesService.remove(it.getId());
                deletedAccountService.delete(it);
            } catch (Exception e) {
                log.error("Error while remove user entities.", e);
            }
        });
        log.info("End deleted account cleaning scheduled task");
    }
}
