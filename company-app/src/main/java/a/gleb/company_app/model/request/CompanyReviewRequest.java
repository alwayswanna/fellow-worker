package a.gleb.company_app.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request to leave a company review.")
public record CompanyReviewRequest(

        @NotNull
        @Min(1)
        @Max(5)
        @Schema(description = "Rating from 1 (worst) to 5 (best).", example = "4")
        Integer rating,

        @Size(max = 3000)
        @Schema(description = "Optional review text.", nullable = true)
        String comment
) {}
