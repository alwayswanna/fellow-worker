package a.gleb.user_app.service;

import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static a.gleb.user_app.constant.UserAppConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationPartitioningService {

    private final JdbcTemplate jdbcTemplate;
    private final UserAppConfigurationProperties properties;

    /**
     * Create partition for `authorization` table based on the configured period ahead.
     * Called by the scheduler.
     */
    public void createPartition() {
        var now = LocalDateTime.now();
        var timeMark = now.plus(properties.createPartitionFor());
        createPartitionForMonth(YearMonth.from(timeMark));
    }

    /**
     * Drop partition for `authorization` table.
     */
    public void dropPartition() {
        var now = LocalDateTime.now();
        var timeMark = now.minus(properties.dropPartitionFor());
        var partitionName = getPartitionName(YearMonth.from(timeMark));
        log.info("AuthorizationPartitioningService, drop partition [partition_name={}]", partitionName);
        jdbcTemplate.update(DROP_PARTITION_QUERY.formatted(partitionName));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createPartitionOnStartup() {
        log.info("AuthorizationPartitioningService, create partition on start-up");
        var currentMonth = YearMonth.now();
        createPartitionForMonth(currentMonth);
        createPartitionForMonth(currentMonth.plusMonths(1));
    }

    public void createPartitionForMonth(YearMonth yearMonth) {
        var from = yearMonth.atDay(1).atStartOfDay();
        var to = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        var partitionName = getPartitionName(yearMonth);
        log.info("AuthorizationPartitioningService, create partition [partition_name={}]", partitionName);
        jdbcTemplate.update(CREATE_PARTITION_QUERY.formatted(partitionName, from, to));
    }

    @NonNull
    private static String getPartitionName(YearMonth yearMonth) {
        return AUTHORIZATION_PARTITION_PATTERN.formatted(yearMonth.getYear(), yearMonth.getMonthValue());
    }
}