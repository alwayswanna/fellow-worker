package a.gleb.apicommon.api.main_service.resume;

import a.gleb.apicommon.api.main_service.EducationDto;
import a.gleb.apicommon.api.main_service.Types.Currency;
import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import a.gleb.apicommon.api.main_service.Types.WorkScheduleEnumeration;
import a.gleb.apicommon.api.main_service.WorkExperienceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;

@Schema(description = "Request for creating or updating a resume")
public record ResumeRequest(
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Resume title", example = "Senior Java Developer",  requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @NotBlank
        @Schema(description = "Detailed description of skills and experience", example = "Experienced Java developer with 8+ years...",  requiredMode = Schema.RequiredMode.REQUIRED)
        String description,

        @NotBlank
        @Size(max = 100)
        @Schema(description = "Full name of the candidate", example = "John Smith",  requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,

        @Email
        @NotBlank
        @Schema(description = "Contact email", example = "john.smith@email.com",  requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$")
        @Schema(description = "Contact phone number", example = "+1234567890")
        String phone,

        @PositiveOrZero
        @Schema(description = "Expected salary", example = "80000")
        Integer salaryExpectation,

        @NotNull(message = "You should set currency of salary")
        @Schema(description = "Currency of expected salary", example = "USD")
        Currency currencyExpectation,

        @PositiveOrZero
        @Schema(description = "Years of experience", example = "8")
        Integer experienceYears,

        @Schema(description = "List of skills", example = "[\"Java\", \"Spring Boot\", \"PostgreSQL\"]")
        Set<String> skills,

        @Schema(description = "Education history")
        Set<EducationDto> educations,

        @Schema(description = "Work experience history")
        Set<WorkExperienceDto> experiences,

        @Schema(description = "Preferred employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Preferred work schedule", example = "FULL_DAY")
        WorkScheduleEnumeration workScheduleEnumeration,

        @Schema(description = "Status of resume", example = "true")
        boolean isActive,

        @Schema(description = "Location preference", example = "New York, USA")
        String location,

        @Schema(description = "Languages spoken", example = "English, Spanish")
        String languages
) {
    public ResumeRequest {
        if (skills == null) skills = Set.of();
        if (educations == null) educations = Set.of();
        if (experiences == null) experiences = Set.of();
    }
}