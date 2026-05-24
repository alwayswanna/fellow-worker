package a.gleb.vacancy_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record VacancyAppConfigurationProperties(
        @NotNull ObservabilityProperty observability,
        @NotNull List<String> unprotectedPatterns
) {

    public record ObservabilityProperty(
            @NotBlank List<String> excludedPatterns
    ) {
    }
}
