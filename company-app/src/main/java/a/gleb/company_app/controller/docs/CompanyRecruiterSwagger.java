package a.gleb.company_app.controller.docs;

import a.gleb.company_app.model.request.AddRecruiterRequest;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.model.response.CompanyRecruiterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Tag(name = "Company Recruiters", description = "Manage recruiters attached to a company")
public interface CompanyRecruiterSwagger {

    @Operation(summary = "Attach a recruiter to the company (OWNER only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recruiter attached"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the owner"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "409", description = "Account is already an owner or recruiter at another company"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    CompanyRecruiterResponse addRecruiter(
            @Parameter(description = "Company ID") UUID companyId,
            AddRecruiterRequest request
    );

    @Operation(summary = "List recruiters of a company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of recruiters"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    PageResponse<CompanyRecruiterResponse> findByCompany(
            @Parameter(description = "Company ID") UUID companyId,
            Pageable pageable
    );

    @Operation(summary = "Remove a recruiter from the company (OWNER only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recruiter removed"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the owner"),
            @ApiResponse(responseCode = "404", description = "Company or recruiter not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    void removeRecruiter(
            @Parameter(description = "Company ID") UUID companyId,
            @Parameter(description = "Account ID of the recruiter to remove") UUID accountId
    );

    @Operation(summary = "Leave the company (recruiter self-removal)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Left the company"),
            @ApiResponse(responseCode = "404", description = "Company not found or you are not a recruiter here"),
            @ApiResponse(responseCode = "409", description = "Cannot leave — you have active vacancies"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    void removeSelf(@Parameter(description = "Company ID") UUID companyId);
}
