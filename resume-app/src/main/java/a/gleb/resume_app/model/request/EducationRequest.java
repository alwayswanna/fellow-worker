package a.gleb.resume_app.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Education entry.")
public record EducationRequest(

        @NotBlank
        @Size(max = 200)
        @Schema(description = "Educational institution name.", example = "Moscow State University")
        String institution,

        @NotBlank
        @Size(max = 150)
        @Schema(description = "Degree obtained.", example = "Bachelor of Science")
        String degree,

        @NotBlank
        @Size(max = 150)
        @Schema(description = "Field of study.", example = "Computer Science")
        String fieldOfStudy,

        @PastOrPresent
        @Schema(description = "Start date of study.", example = "2016-09-01")
        LocalDate startDate,

        @Schema(description = "End date of study. Null means currently studying.", example = "2020-06-30", nullable = true)
        LocalDate endDate
) {
}