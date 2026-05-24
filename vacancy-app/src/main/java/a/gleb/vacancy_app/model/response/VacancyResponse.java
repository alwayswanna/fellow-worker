package a.gleb.vacancy_app.model.response;

import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Vacancy details returned by the API.")
public record VacancyResponse(

        @Schema(description = "Unique identifier.")
        UUID id,

        @Schema(description = "ID of the company that posted this vacancy.")
        UUID companyId,

        @Schema(description = "Job title.", example = "Senior Backend Developer")
        String title,

        @Schema(description = "Full job description.", nullable = true)
        String description,

        @Schema(description = "Requirements for the candidate.", nullable = true)
        String requirements,

        @Schema(description = "Minimum salary offered.", nullable = true)
        Long salaryFrom,

        @Schema(description = "Maximum salary offered.", nullable = true)
        Long salaryTo,

        @Schema(description = "Salary currency code.", example = "RUB", nullable = true)
        String currency,

        @Schema(description = "Type of employment.", nullable = true)
        EmploymentType employmentType,

        @Schema(description = "Work format.", nullable = true)
        WorkFormat workFormat,

        @Schema(description = "Required experience level.", nullable = true)
        ExperienceLevel experienceLevel,

        @Schema(description = "City.", nullable = true)
        String city,

        @Schema(description = "Country.", nullable = true)
        String country,

        @Schema(description = "Current status of the vacancy.")
        VacancyStatus status,

        @Schema(description = "Required skills.", nullable = true)
        List<String> skills,

        @Schema(description = "Timestamp when the vacancy was created.")
        Instant createdAt,

        @Schema(description = "Timestamp of the last update.", nullable = true)
        Instant updatedAt,

        @Schema(description = "Contact person name.", nullable = true)
        String contactName,

        @Schema(description = "Contact email.", nullable = true)
        String contactEmail,

        @Schema(description = "Contact phone.", nullable = true)
        String contactPhone
) {}
