package a.gleb.company_app.model.response;

import a.gleb.company_app.model.enums.CompanySize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
@Schema(description = "Company details returned by the API.")
public record CompanyResponse(

        @Schema(description = "Unique identifier.")
        UUID id,

        @Schema(description = "Account ID of the company owner.")
        UUID ownerAccountId,

        @Schema(description = "Company name.", example = "Yandex")
        String name,

        @Schema(description = "Company description.", nullable = true)
        String description,

        @Schema(description = "Company website URL.", nullable = true)
        String website,

        @Schema(description = "Logo URL.", nullable = true)
        String logoUrl,

        @Schema(description = "Industry sector.", nullable = true)
        String industry,

        @Schema(description = "Company size tier.", nullable = true)
        CompanySize size,

        @Schema(description = "City of headquarters.", nullable = true)
        String city,

        @Schema(description = "Country of headquarters.", nullable = true)
        String country,

        @Schema(description = "Average rating based on employee reviews (0.0 – 5.0).")
        double rating,

        @Schema(description = "Total number of reviews.")
        int reviewCount,

        @Schema(description = "Timestamp when the company was created.")
        Instant createdAt,

        @Schema(description = "Timestamp of the last update.", nullable = true)
        Instant updatedAt
) {}
