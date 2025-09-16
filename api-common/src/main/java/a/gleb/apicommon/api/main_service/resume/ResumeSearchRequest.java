package a.gleb.apicommon.api.main_service.resume;

import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.util.Set;

@Schema(description = "Search parameters for resumes")
public record ResumeSearchRequest(
        @Schema(description = "Search query text", example = "Java developer")
        String query,

        @Min(0)
        @Schema(description = "Minimum years of experience", example = "3")
        Integer minExperience,

        @Min(0)
        @Schema(description = "Maximum years of experience", example = "10")
        Integer maxExperience,

        @Min(0)
        @Schema(description = "Minimum salary expectation", example = "50000")
        Integer minSalary,

        @Min(0)
        @Schema(description = "Maximum salary expectation", example = "100000")
        Integer maxSalary,

        @Schema(description = "Employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Location", example = "New York")
        String location,

        @Schema(description = "Required skills", example = "[\"Java\", \"Spring\"]")
        Set<String> skills,

        @Min(0)
        @Schema(description = "Page number", example = "0")
        Integer page,

        @Min(1)
        @Schema(description = "Page size", example = "10")
        Integer size
) {
    public ResumeSearchRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
    }
}
