package a.gleb.resume_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record ResumeAppConfigurationProperties(
        @NotNull Minio minio,
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
}
