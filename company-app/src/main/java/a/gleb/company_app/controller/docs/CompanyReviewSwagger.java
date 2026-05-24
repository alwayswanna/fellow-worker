package a.gleb.company_app.controller.docs;

import a.gleb.company_app.model.request.CompanyReviewRequest;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.model.response.CompanyReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Tag(name = "Company Reviews", description = "Leave and browse company reviews")
public interface CompanyReviewSwagger {

    @Operation(summary = "Leave a review for a company")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "409", description = "You have already reviewed this company"),
            @ApiResponse(responseCode = "422", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    CompanyReviewResponse create(
            @Parameter(description = "Company ID") UUID companyId,
            CompanyReviewRequest request
    );

    @Operation(summary = "List reviews for a company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of reviews"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    PageResponse<CompanyReviewResponse> findByCompany(
            @Parameter(description = "Company ID") UUID companyId,
            Pageable pageable
    );

    @Operation(summary = "Delete your own review")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    void delete(
            @Parameter(description = "Company ID") UUID companyId,
            @Parameter(description = "Review ID") UUID reviewId
    );
}
