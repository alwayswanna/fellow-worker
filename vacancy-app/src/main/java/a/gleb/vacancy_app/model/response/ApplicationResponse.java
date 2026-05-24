package a.gleb.vacancy_app.model.response;

import a.gleb.vacancy_app.model.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Application (response to a vacancy) details.")
public record ApplicationResponse(

        @Schema(description = "Application ID.")
        UUID id,

        @Schema(description = "Vacancy ID.")
        UUID vacancyId,

        @Schema(description = "Account ID of the applicant.")
        UUID applicantAccountId,

        @Schema(description = "Current application status.")
        ApplicationStatus status,

        @Schema(description = "When the application was submitted.")
        Instant createdAt,

        @Schema(description = "When the status was last updated.", nullable = true)
        Instant updatedAt
) {}
