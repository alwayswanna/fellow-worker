package a.gleb.apicommon.api.main_service;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "Education information")
public record EducationDto(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Educational institution name", example = "MIT",  requiredMode = Schema.RequiredMode.REQUIRED)
        String institution,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Degree obtained", example = "Bachelor",  requiredMode = Schema.RequiredMode.REQUIRED)
        String degree,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Field of study", example = "Computer Science",  requiredMode = Schema.RequiredMode.REQUIRED)
        String fieldOfStudy,

        @NotNull
        @Schema(description = "Start date of education", example = "2015-09-01",  requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startDate,

        @Schema(description = "End date of education (null if current)", example = "2019-06-01")
        LocalDate endDate,

        @Schema(description = "Whether this education is current", example = "false")
        Boolean isCurrent,

        @Schema(description = "Additional description of education", example = "Specialized in software engineering")
        String description
) {
    @Builder
    public EducationDto {
        if (isCurrent == null) isCurrent = false;
    }
}
