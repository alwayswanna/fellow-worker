package a.gleb.resume_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@ConfigurationProperties("app")
public record ResumeAppConfigurationProperties(
        @NotNull Cors cors,
        @NotNull Minio minio,
        @NotBlank List<String> unprotectedPatterns,
        @NotNull ObservabilityProperty observability
) {

    public record ObservabilityProperty(
            @NotBlank List<String> excludedPatterns
    ) {
    }

    public record Minio(
            @NotBlank String endpoint,
            @NotBlank String accessKey,
            @NotBlank String secretKey,
            @NotBlank String bucket
    ) {
    }

    public static class Cors extends CorsConfiguration {}
}
