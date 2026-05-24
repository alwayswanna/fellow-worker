package a.gleb.vacancy_app.controller.docs;

import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.vacancy_app.model.request.VacancyRequest;
import a.gleb.vacancy_app.model.response.VacancyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Vacancies", description = "CRUD and search operations for vacancies")
public interface VacancySwagger {

    @Operation(summary = "Create a vacancy")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    VacancyResponse create(VacancyRequest request);

    @Operation(summary = "Get a vacancy by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    VacancyResponse findById(@Parameter(description = "Vacancy ID") UUID id);

    @Operation(summary = "Search vacancies with optional filters")
    @ApiResponse(responseCode = "200", description = "Page of vacancies")
    PageResponse<VacancyResponse> search(
            @Parameter(description = "Title search (partial match)") String title,
            @Parameter(description = "Filter by company") UUID companyId,
            @Parameter(description = "City filter") String city,
            @Parameter(description = "Employment type filter") EmploymentType employmentType,
            @Parameter(description = "Work format filter") WorkFormat workFormat,
            @Parameter(description = "Experience level filter") ExperienceLevel experienceLevel,
            @Parameter(description = "Minimum salary boundary") Long salaryFrom,
            @Parameter(description = "Maximum salary boundary") Long salaryTo,
            @Parameter(description = "Status filter (default ACTIVE)") VacancyStatus status,
            @Parameter(description = "Required skills (any match)") List<String> skills,
            Pageable pageable
    );

    @Operation(summary = "Update a vacancy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    VacancyResponse update(@Parameter(description = "Vacancy ID") UUID id, VacancyRequest request);

    @Operation(summary = "Delete a vacancy")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    void delete(@Parameter(description = "Vacancy ID") UUID id);
}
