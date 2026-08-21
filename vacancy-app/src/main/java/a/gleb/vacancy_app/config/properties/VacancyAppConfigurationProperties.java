package a.gleb.vacancy_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record VacancyAppConfigurationProperties(
        @NotNull ObservabilityProperty observability,
        @NotNull List<String> unprotectedPatterns,
        @NotNull CompanyServiceProperty companyService
) {

    public record ObservabilityProperty(
            @NotBlank List<String> excludedPatterns
    ) {
    }

    /**
     * Base URL of company-app, used to enrich vacancy responses with the
     * posting company's name/logo/city/rating.
     */
    public record CompanyServiceProperty(
            @NotBlank String baseUrl
    ) {
    }
}
