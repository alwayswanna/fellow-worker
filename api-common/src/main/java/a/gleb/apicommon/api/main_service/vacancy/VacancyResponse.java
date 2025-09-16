package a.gleb.apicommon.api.main_service.vacancy;

import a.gleb.apicommon.api.main_service.Types;
import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import a.gleb.apicommon.api.main_service.Types.ExperienceLevelEnumeration;
import a.gleb.apicommon.api.main_service.Types.WorkScheduleEnumeration;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Response with vacancy data")
public record VacancyResponse(
        @Schema(description = "Vacancy ID", example = "1")
        Long id,

        @Schema(description = "Vacancy title", example = "Senior Java Developer")
        String title,

        @Schema(description = "Detailed job description")
        String description,

        @Schema(description = "Company name", example = "TechCorp Inc.")
        String companyName,

        @Schema(description = "Contact email", example = "hr@techcorp.com")
        String contactEmail,

        @Schema(description = "Minimum salary", example = "70000")
        Integer salaryFrom,

        @Schema(description = "Maximum salary", example = "90000")
        Integer salaryTo,

        @Schema(description = "Salary currency", example = "USD")
        Types.Currency salaryCurrency,

        @Schema(description = "Job requirements", example = "[\"Java 11+\", \"Spring Boot\"]")
        Set<String> requirements,

        @Schema(description = "Company benefits", example = "[\"Health insurance\"]")
        Set<String> benefits,

        @Schema(description = "Employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Work schedule", example = "FULL_DAY")
        WorkScheduleEnumeration workScheduleEnumeration,

        @Schema(description = "Experience level", example = "SENIOR")
        ExperienceLevelEnumeration experienceLevelEnumeration,

        @Schema(description = "Job location", example = "New York, USA")
        String location,

        @Schema(description = "Whether the job is remote", example = "true")
        Boolean isRemote,

        @Schema(description = "Whether the vacancy is active", example = "true")
        Boolean isActive,

        @Schema(description = "Application deadline", example = "2024-03-31")
        LocalDate applicationDeadline,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {}
