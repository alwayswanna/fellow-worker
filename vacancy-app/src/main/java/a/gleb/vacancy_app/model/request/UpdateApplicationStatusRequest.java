package a.gleb.vacancy_app.model.request;

import a.gleb.vacancy_app.model.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update an application status.")
public record UpdateApplicationStatusRequest(

        @NotNull
        @Schema(description = "New status. Allowed values for recruiter: REVIEWED, ACCEPTED, REJECTED.")
        ApplicationStatus status
) {}
