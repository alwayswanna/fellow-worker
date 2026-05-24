package a.gleb.user_app.scheduler;

import a.gleb.user_app.service.AuthorizationPartitioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationPartitioningScheduler {

    private final AuthorizationPartitioningService authorizationPartitioningService;

    @Scheduled(cron = "${app.schedulers.create-partition}")
    @SchedulerLock(
            name = "lockCreatePartition",
            lockAtLeastFor = "${app.create-partition.lock.at-least-for}",
            lockAtMostFor = "${app.create-partition.lock.at-most-for}"
    )
    public void createPartition() {
        try {
            authorizationPartitioningService.createPartition();
        } catch (Exception e) {
            log.error("AuthorizationPartitioningScheduler: create partition failure, [message={}]", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "${app.schedulers.drop-partition}")
    @SchedulerLock(
            name = "lockDropPartition",
            lockAtLeastFor = "${app.drop-partition.lock.at-least-for}",
            lockAtMostFor = "${app.drop-partition.lock.at-most-for}"
    )
    public void dropPartition() {
        try {
            authorizationPartitioningService.dropPartition();
        } catch (Exception e) {
            log.error("AuthorizationPartitioningScheduler: drop partition failure, [message={}]", e.getMessage(), e);
        }
    }
}
