package a.gleb.resume_app.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "Work experience entry.")
public record WorkExperienceResponse(

        @Schema(description = "Company name.", example = "Google")
        String company,

        @Schema(description = "Job title / position.", example = "Senior Software Engineer")
        String position,

        @Schema(description = "Start date of employment.", example = "2020-03-01")
        LocalDate startDate,

        @Schema(description = "End date of employment. Null means current job.", example = "2023-06-30", nullable = true)
        LocalDate endDate,

        @Schema(description = "Description of responsibilities and achievements.", example = "Developed high-load microservices...")
        String description
) {
}