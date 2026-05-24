package a.gleb.resume_app.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request to create or update a resume.")
public record ResumeRequest(

        @NotBlank
        @Size(max = 100)
        @Schema(description = "First name.", example = "Ivan")
        String firstName,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Last name.", example = "Ivanov")
        String lastName,

        @NotBlank
        @Email
        @Size(max = 254)
        @Schema(description = "Contact email address.", example = "ivan.ivanov@example.com")
        String email,

        @Pattern(regexp = "^\\+?[0-9 ()\\-]{7,20}$", message = "Invalid phone number format")
        @Schema(description = "Contact phone number.", example = "+7 (999) 123-45-67", nullable = true)
        String phone,

        @Size(max = 100)
        @Schema(description = "Desired job title.", example = "Backend Developer", nullable = true)
        String desiredPosition,

        @Size(max = 3000)
        @Schema(description = "Professional summary / about section.", example = "Experienced Java developer with 5+ years...", nullable = true)
        String summary,

        @Past
        @Schema(description = "Date of birth.", example = "1990-05-15", nullable = true)
        LocalDate birthDate,

        @NotEmpty
        @Size(max = 50)
        @Schema(description = "List of professional skills.", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
        List<@NotBlank @Size(max = 100) String> skills,

        @Valid
        @Schema(description = "Work experience entries.", nullable = true)
        List<WorkExperienceRequest> experience,

        @Valid
        @Schema(description = "Education entries.", nullable = true)
        List<EducationRequest> education,

        @Schema(description = "Links to external profiles (e.g. LinkedIn, GitHub).", nullable = true)
        List<@NotBlank @Size(max = 500) String> links
) {
}