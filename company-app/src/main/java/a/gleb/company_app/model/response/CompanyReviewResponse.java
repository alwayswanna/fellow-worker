package a.gleb.company_app.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Company review returned by the API.")
public record CompanyReviewResponse(

        @Schema(description = "Unique identifier.")
        UUID id,

        @Schema(description = "ID of the company being reviewed.")
        UUID companyId,

        @Schema(description = "ID of the account that wrote the review.")
        UUID accountId,

        @Schema(description = "Rating from 1 to 5.")
        int rating,

        @Schema(description = "Review text.", nullable = true)
        String comment,

        @Schema(description = "Timestamp when the review was created.")
        Instant createdAt
) {}
