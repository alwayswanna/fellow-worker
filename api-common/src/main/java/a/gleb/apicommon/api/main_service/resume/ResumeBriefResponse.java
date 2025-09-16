package a.gleb.apicommon.api.main_service.resume;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Response with brief resume information")
public record ResumeBriefResponse(
        @Schema(description = "Resume ID", example = "1")
        Long id,

        @Schema(description = "Resume title", example = "Senior Java Developer")
        String title,

        @Schema(description = "Full name of the candidate", example = "John Smith")
        String fullName,

        @Schema(description = "Years of experience", example = "8")
        Integer experienceYears,

        @Schema(description = "List of main skills", example = "[\"Java\", \"Spring Boot\"]")
        Set<String> skills,

        @Schema(description = "Location preference", example = "New York, USA")
        String location,

        @Schema(description = "Expected salary", example = "80000")
        Integer salaryExpectation,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt
) {}
