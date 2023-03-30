/*
 * Copyright (c) 3-3/30/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.scheduler;

import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@AllArgsConstructor
public class RemoveExpiredOauth2AuthorizationScheduledTask {

    private static final String SQL_REMOVE_EXPIRED_SESSIONS =
            """
                delete from oauth2_authorization where refresh_token_expires_at < (?);
            """;

    private final JdbcTemplate jdbcTemplate;
    private final OAuth2ServerProperties properties;

    /**
     * Cron job for remove expired session.
     */
    @Scheduled(cron = "${oauth.remove-expired-session-task.cron}")
    public void removeExpiredSessions() {
        log.info("Start scheduled task to remove expired sessions");
        var isBeforeParamSql = LocalDate.now()
                .minus(properties.getRemoveExpiredSessionTask().getDayOffset(), ChronoUnit.DAYS)
                .toString()
                .lines()
                .toArray();

        try {
            var sqlRowResult = jdbcTemplate.update(
                    SQL_REMOVE_EXPIRED_SESSIONS,
                    isBeforeParamSql
            );
            log.info("SQL row updated: {}", sqlRowResult);
        } catch (Exception e) {
            log.error("Error in scheduled task to remove expired sessions, error=", e);
            throw e;
        }

        log.info("End scheduled task to remove expired sessions");
    }
}
