package a.gleb.user_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.time.Period;
import java.util.List;
import java.util.Set;

@ConfigurationProperties("app")
public record UserAppConfigurationProperties(
        @NotNull DefaultRole role,
        @NotNull DefaultUser admin,
        @NotNull MinioProperties minio,
        @NotNull OutboxProperties outbox,
        @NotNull Integer encoderStrength,
        @NotNull Period dropPartitionFor,
        @NotNull Period createPartitionFor,
        @NotEmpty Set<String> unprotectedPatterns,
        @NotNull ObservabilityProperty observability,
        @NotNull List<SecurityConstraint> securityConstraints,
        @NotNull RegisteredClientDefaultOptions registeredClientDefaultOptions
) {

    public record ObservabilityProperty(
            @NotBlank List<String> excludedPatterns
    ) {
    }

    /**
     * Data on create admin user or another default user.
     */
    public record DefaultUser(
            @NotNull String roleCode,
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record DefaultRole(
            @NotBlank String roleCode,
            @NotBlank String roleName
    ) {
    }

    public record RegisteredClientDefaultOptions(
            @NotNull Period clientSecretExpiresAt,
            @NotNull Duration accessTokenTimeToLive
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

    public record SecurityConstraint(
            List<SecurityCollection> securityCollections,
            Set<String> roles
    ) {
    }

    public record SecurityCollection(
            Set<String> patterns,
            Set<String> methods
    ) {
    }

    public record MinioProperties(
            @NotBlank String endpoint,
            @NotBlank String accessKey,
            @NotBlank String secretKey,
            @NotBlank String bucket
    ) {
    }
}
