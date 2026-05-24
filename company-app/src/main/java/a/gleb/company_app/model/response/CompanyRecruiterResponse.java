package a.gleb.company_app.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Recruiter attached to a company.")
public record CompanyRecruiterResponse(

        @Schema(description = "Unique identifier of the recruiter entry.")
        UUID id,

        @Schema(description = "Company ID.")
        UUID companyId,

        @Schema(description = "Account ID of the recruiter.")
        UUID accountId,

        @Schema(description = "Timestamp when the recruiter was attached.")
        Instant joinedAt
) {}
