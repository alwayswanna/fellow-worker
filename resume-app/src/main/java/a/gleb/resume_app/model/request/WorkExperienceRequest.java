package a.gleb.resume_app.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Work experience entry.")
public record WorkExperienceRequest(

        @NotBlank
        @Size(max = 150)
        @Schema(description = "Company name.", example = "Google")
        String company,

        @NotBlank
        @Size(max = 150)
        @Schema(description = "Job title / position.", example = "Senior Software Engineer")
        String position,

        @PastOrPresent
        @Schema(description = "Start date of employment.", example = "2020-03-01")
        LocalDate startDate,

        @Schema(description = "End date of employment. Null means current job.", example = "2023-06-30", nullable = true)
        LocalDate endDate,

        @Size(max = 2000)
        @Schema(description = "Description of responsibilities and achievements.", example = "Developed high-load microservices...")
        String description
) {
}