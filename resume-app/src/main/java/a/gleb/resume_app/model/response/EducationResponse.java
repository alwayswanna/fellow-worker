package a.gleb.resume_app.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "Education entry.")
public record EducationResponse(

        @Schema(description = "Educational institution name.", example = "Moscow State University")
        String institution,

        @Schema(description = "Degree obtained.", example = "Bachelor of Science")
        String degree,

        @Schema(description = "Field of study.", example = "Computer Science")
        String fieldOfStudy,

        @Schema(description = "Start date of study.", example = "2016-09-01")
        LocalDate startDate,

        @Schema(description = "End date of study. Null means currently studying.", example = "2020-06-30", nullable = true)
        LocalDate endDate
) {
}