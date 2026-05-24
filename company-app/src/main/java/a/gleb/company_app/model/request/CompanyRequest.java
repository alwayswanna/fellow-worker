package a.gleb.company_app.model.request;

import a.gleb.company_app.model.enums.CompanySize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to create or update a company.")
public record CompanyRequest(

        @NotBlank
        @Size(max = 200)
        @Schema(description = "Company name.", example = "Yandex")
        String name,

        @Size(max = 5000)
        @Schema(description = "Company description.", nullable = true)
        String description,

        @Size(max = 500)
        @Schema(description = "Company website URL.", example = "https://yandex.ru", nullable = true)
        String website,

        @Size(max = 100)
        @Schema(description = "Industry sector.", example = "IT / Software Development", nullable = true)
        String industry,

        @Schema(description = "Company size tier.", nullable = true)
        CompanySize size,

        @Size(max = 100)
        @Schema(description = "City of headquarters.", example = "Moscow", nullable = true)
        String city,

        @Size(max = 100)
        @Schema(description = "Country of headquarters.", example = "Russia", nullable = true)
        String country
) {}
