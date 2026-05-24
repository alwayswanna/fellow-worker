package a.gleb.vacancy_app.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app")
public record VacancyAppConfigurationProperties(
        @NotNull ObservabilityProperty observability,
        @NotNull CompanyServiceProperty companyService,
        @NotNull ResumeServiceProperty resumeService
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

    /**
     * Base URL of resume-app, used to check whether an applicant has at least one resume
     * before allowing them to apply to a vacancy.
     */
    public record ResumeServiceProperty(
            @NotBlank String baseUrl
    ) {
    }
}
