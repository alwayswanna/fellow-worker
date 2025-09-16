package a.gleb.apicommon.api.main_service.vacancy;

import a.gleb.apicommon.api.main_service.Types.EmploymentTypeEnumeration;
import a.gleb.apicommon.api.main_service.Types.ExperienceLevelEnumeration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.util.Set;

@Schema(description = "Search parameters for vacancies")
public record VacancySearchRequest(
        @Schema(description = "Search query text", example = "Java developer")
        String query,

        @Min(0)
        @Schema(description = "Minimum salary", example = "50000")
        Integer minSalary,

        @Min(0)
        @Schema(description = "Maximum salary", example = "100000")
        Integer maxSalary,

        @Schema(description = "Experience level", example = "SENIOR")
        ExperienceLevelEnumeration experienceLevelEnumeration,

        @Schema(description = "Employment type", example = "FULL_TIME")
        EmploymentTypeEnumeration employmentTypeEnumeration,

        @Schema(description = "Location", example = "New York")
        String location,

        @Schema(description = "Remote work available", example = "true")
        Boolean isRemote,

        @Schema(description = "Required skills", example = "[\"Java\", \"Spring\"]")
        Set<String> skills,

        @Min(0)
        @Schema(description = "Page number", example = "0")
        Integer page,

        @Min(1)
        @Schema(description = "Page size", example = "10")
        Integer size
) {
    public VacancySearchRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
    }
}
