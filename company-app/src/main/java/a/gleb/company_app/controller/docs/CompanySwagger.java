package a.gleb.company_app.controller.docs;

import a.gleb.company_app.model.enums.CompanySize;
import a.gleb.company_app.model.request.CompanyRequest;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.model.response.CompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Tag(name = "Companies", description = "CRUD operations for companies")
public interface CompanySwagger {

    @Operation(summary = "Create a company (current user becomes OWNER)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "409", description = "User already owns or is a recruiter at another company"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    CompanyResponse create(CompanyRequest request);

    @Operation(summary = "Get a company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    CompanyResponse findById(@Parameter(description = "Company ID") UUID id);

    @Operation(summary = "List companies with optional filters")
    @ApiResponse(responseCode = "200", description = "Page of companies")
    PageResponse<CompanyResponse> findAll(
            @Parameter(description = "Name search (partial match)") String name,
            @Parameter(description = "Industry filter") String industry,
            @Parameter(description = "City filter") String city,
            @Parameter(description = "Size filter") CompanySize size,
            Pageable pageable
    );

    @Operation(summary = "Update a company (OWNER only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the owner"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    CompanyResponse update(@Parameter(description = "Company ID") UUID id, CompanyRequest request);

    @Operation(summary = "Delete a company (OWNER only, no active recruiters or vacancies)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the owner"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Company still has recruiters or vacancies"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    void delete(@Parameter(description = "Company ID") UUID id);
}
