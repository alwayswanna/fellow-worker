package a.gleb.apicommon.api.main_service.resume;

import a.gleb.apicommon.api.main_service.EducationDto;
import a.gleb.apicommon.api.main_service.Types.Currency;
import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import a.gleb.apicommon.api.main_service.Types.WorkScheduleEnumeration;
import a.gleb.apicommon.api.main_service.WorkExperienceDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Response with resume data")
public record ResumeResponse(
        @Schema(description = "Resume ID", example = "1")
        UUID id,

        @Schema(description = "Resume title", example = "Senior Java Developer")
        String title,

        @Schema(description = "Detailed description")
        String description,

        @Schema(description = "Full name of the candidate", example = "John Smith")
        String fullName,

        @Schema(description = "Contact email", example = "john.smith@email.com")
        String email,

        @Schema(description = "Contact phone number", example = "+1234567890")
        String phone,

        @Schema(description = "Expected salary", example = "80000")
        Integer salaryExpectation,

        @Schema(description = "Expected salary currency", example = "USD")
        Currency expectedSalaryCurrency,

        @Schema(description = "Years of experience", example = "8")
        Integer experienceYears,

        @Schema(description = "List of skills", example = "[\"Java\", \"Spring Boot\"]")
        Set<String> skills,

        @Schema(description = "Education history")
        Set<EducationDto> educations,

        @Schema(description = "Work experience history")
        Set<WorkExperienceDto> experiences,

        @Schema(description = "Employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Work schedule", example = "FULL_DAY")
        WorkScheduleEnumeration workScheduleEnumeration,

        @Schema(description = "Location preference", example = "New York, USA")
        String location,

        @Schema(description = "Languages spoken", example = "English, Spanish")
        String languages,

        @Schema(description = "Whether the resume is active", example = "true")
        Boolean isActive,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Last update timestamp")
        LocalDateTime updatedAt
) {

    @Builder public ResumeResponse {}
}
