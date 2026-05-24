package a.gleb.resume_app.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Resume details returned by the API.")
public record ResumeResponse(

        @Schema(description = "Unique identifier of the resume.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "First name.", example = "Ivan")
        String firstName,

        @Schema(description = "Last name.", example = "Ivanov")
        String lastName,

        @Schema(description = "Contact email address.", example = "ivan.ivanov@example.com")
        String email,

        @Schema(description = "Contact phone number.", example = "+7 (999) 123-45-67", nullable = true)
        String phone,

        @Schema(description = "Desired job title.", example = "Backend Developer", nullable = true)
        String desiredPosition,

        @Schema(description = "Professional summary / about section.", nullable = true)
        String summary,

        @Schema(description = "Date of birth.", example = "1990-05-15", nullable = true)
        LocalDate birthDate,

        @Schema(description = "URL of the applicant's photo.", nullable = true)
        String photoUrl,

        @Schema(description = "List of professional skills.", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
        List<String> skills,

        @Schema(description = "Work experience entries.", nullable = true)
        List<WorkExperienceResponse> experience,

        @Schema(description = "Education entries.", nullable = true)
        List<EducationResponse> education,

        @Schema(description = "Links to external profiles (e.g. LinkedIn, GitHub).", nullable = true)
        List<String> links,

        @Schema(description = "Timestamp when the resume was created.")
        Instant createdAt,

        @Schema(description = "Timestamp of the last update.")
        Instant updatedAt
) {
}