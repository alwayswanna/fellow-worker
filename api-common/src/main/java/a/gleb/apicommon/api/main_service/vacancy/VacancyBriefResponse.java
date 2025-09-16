package a.gleb.apicommon.api.main_service.vacancy;

import a.gleb.apicommon.api.main_service.Types;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Response with brief vacancy information")
public record VacancyBriefResponse(
        @Schema(description = "Vacancy ID", example = "1")
        Long id,

        @Schema(description = "Vacancy title", example = "Senior Java Developer")
        String title,

        @Schema(description = "Company name", example = "TechCorp Inc.")
        String companyName,

        @Schema(description = "Salary range", example = "70000-90000 USD")
        String salaryRange,

        @Schema(description = "Location", example = "New York, USA")
        String location,

        @Schema(description = "Whether the job is remote", example = "true")
        Boolean isRemote,

        @Schema(description = "Experience level", example = "SENIOR")
        Types.ExperienceLevelEnumeration experienceLevelEnumeration,

        @Schema(description = "Main requirements", example = "[\"Java\", \"Spring Boot\"]")
        Set<String> requirements,

        @Schema(description = "Creation timestamp")
        LocalDateTime createdAt
) {}
