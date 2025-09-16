package a.gleb.apicommon.api.main_service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "Work experience information")
public record WorkExperienceDto(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Company name", example = "Google",  requiredMode = Schema.RequiredMode.REQUIRED)
        String company,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Position title", example = "Senior Developer",  requiredMode = Schema.RequiredMode.REQUIRED)
        String position,

        @NotNull
        @Schema(description = "Start date of employment", example = "2020-01-15",  requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startDate,

        @Schema(description = "End date of employment (null if current)", example = "2022-12-31")
        LocalDate endDate,

        @Schema(description = "Whether this experience is current", example = "true")
        Boolean isCurrent,

        @Schema(description = "Description of responsibilities and achievements", example = "Led a team of 5 developers")
        String description
) {
    @Builder
    public WorkExperienceDto {
        if (isCurrent == null) isCurrent = false;
    }
}
