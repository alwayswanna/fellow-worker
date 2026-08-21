package a.gleb.company_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record CompanyAppConfigurationProperties(
        @NotNull ObservabilityProperty observability,
        @NotNull List<String> unprotectedPatterns,
        @NotNull OutboxProperties outbox
) {
    public record ObservabilityProperty(
            @NotBlank List<String> excludedPatterns
    ) {
    }

    public record OutboxProperties(
            @NotNull SchedulerProperties scheduler,
            @NotNull LockProperties lock,
            @Positive int batchSize,
            @Positive int maxRetries
    ) {
        public record SchedulerProperties(@NotBlank String cron) {
        }

        public record LockProperties(
                @NotBlank String atMostFor,
                @NotBlank String atLeastFor
        ) {
        }
    }
}
