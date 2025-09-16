package a.gleb.apicommon.api.main_service.vacancy;

import a.gleb.apicommon.api.main_service.Types;
import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import a.gleb.apicommon.api.main_service.Types.ExperienceLevelEnumeration;
import a.gleb.apicommon.api.main_service.Types.WorkScheduleEnumeration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Request for creating or updating a vacancy")
public record VacancyRequest(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Vacancy title", example = "Senior Java Developer",  requiredMode = REQUIRED)
        String title,

        @NotBlank
        @Schema(description = "Detailed job description", example = "We are looking for an experienced Java developer...",  requiredMode = REQUIRED)
        String description,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Company name", example = "TechCorp Inc.",  requiredMode = REQUIRED)
        String companyName,

        @Email
        @Schema(description = "Contact email for applications", example = "hr@techcorp.com")
        String contactEmail,

        @PositiveOrZero
        @Schema(description = "Minimum salary offered", example = "70000")
        Integer salaryFrom,

        @PositiveOrZero
        @Schema(description = "Maximum salary offered", example = "90000")
        Integer salaryTo,

        @Schema(description = "Salary currency", example = "USD")
        Types.Currency salaryCurrency,

        @Schema(description = "Job requirements", example = "[\"Java 11+\", \"Spring Boot\", \"5+ years experience\"]")
        Set<String> requirements,

        @Schema(description = "Company benefits", example = "[\"Health insurance\", \"Remote work\"]")
        Set<String> benefits,

        @Schema(description = "Employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Work schedule", example = "FULL_DAY")
        WorkScheduleEnumeration workScheduleEnumeration,

        @Schema(description = "Required experience level", example = "SENIOR")
        ExperienceLevelEnumeration experienceLevelEnumeration,

        @Schema(description = "Job location", example = "New York, USA")
        String location,

        @Schema(description = "Whether the job is remote", example = "true")
        Boolean isRemote,

        @Schema(description = "Application deadline", example = "2024-03-31")
        LocalDate applicationDeadline
) {
    public VacancyRequest {
        if (requirements == null) requirements = Set.of();
        if (benefits == null) benefits = Set.of();
        if (isRemote == null) isRemote = false;
    }
}