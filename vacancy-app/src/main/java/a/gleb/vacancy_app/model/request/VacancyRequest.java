package a.gleb.vacancy_app.model.request;

import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

@Schema(description = "Request to create or update a vacancy.")
public record VacancyRequest(

        @NotNull
        @Schema(description = "ID of the company posting this vacancy.")
        UUID companyId,

        @NotBlank
        @Size(max = 200)
        @Schema(description = "Job title.", example = "Senior Backend Developer")
        String title,

        @Size(max = 10000)
        @Schema(description = "Full job description.", nullable = true)
        String description,

        @Size(max = 5000)
        @Schema(description = "Requirements for the candidate.", nullable = true)
        String requirements,

        @PositiveOrZero
        @Schema(description = "Minimum salary offered.", example = "150000", nullable = true)
        Long salaryFrom,

        @PositiveOrZero
        @Schema(description = "Maximum salary offered.", example = "250000", nullable = true)
        Long salaryTo,

        @Size(max = 10)
        @Schema(description = "Salary currency code.", example = "RUB", nullable = true)
        String currency,

        @Schema(description = "Type of employment.", nullable = true)
        EmploymentType employmentType,

        @Schema(description = "Work format.", nullable = true)
        WorkFormat workFormat,

        @Schema(description = "Required experience level.", nullable = true)
        ExperienceLevel experienceLevel,

        @Size(max = 100)
        @Schema(description = "City.", example = "Moscow", nullable = true)
        String city,

        @Size(max = 100)
        @Schema(description = "Country.", example = "Russia", nullable = true)
        String country,

        @NotNull
        @Schema(description = "Current status of the vacancy.")
        VacancyStatus status,

        @Size(max = 30)
        @Schema(description = "Required skills / technology tags.", example = "[\"Java\", \"Spring Boot\", \"Kafka\"]", nullable = true)
        List<@NotBlank @Size(max = 100) String> skills,

        @Size(max = 200)
        @Schema(description = "Contact person name.", nullable = true)
        String contactName,

        @Email
        @Size(max = 200)
        @Schema(description = "Contact email.", nullable = true)
        String contactEmail,

        @Size(max = 50)
        @Schema(description = "Contact phone.", nullable = true)
        String contactPhone
) {}
