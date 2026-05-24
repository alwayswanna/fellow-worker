package a.gleb.company_app.controller;

import a.gleb.company_app.controller.docs.CompanyReviewSwagger;
import a.gleb.company_app.model.request.CompanyReviewRequest;
import a.gleb.company_app.model.response.CompanyReviewResponse;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.company_app.service.CompanyReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/companies/{companyId}/reviews")
public class CompanyReviewController implements CompanyReviewSwagger {

    private final CompanyReviewService reviewService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyReviewResponse create(
            @PathVariable UUID companyId,
            @Valid @RequestBody CompanyReviewRequest request
    ) {
        return reviewService.create(companyId, request);
    }

    @Override
    @GetMapping
    public PageResponse<CompanyReviewResponse> findByCompany(
            @PathVariable UUID companyId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var p = reviewService.findByCompany(companyId, pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID companyId, @PathVariable UUID reviewId) {
        reviewService.delete(companyId, reviewId);
    }
}
