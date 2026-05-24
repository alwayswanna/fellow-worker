package a.gleb.company_app.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to attach a recruiter to the company.")
public record AddRecruiterRequest(

        @NotNull
        @Schema(description = "Account ID of the recruiter to attach.")
        UUID accountId
) {}
