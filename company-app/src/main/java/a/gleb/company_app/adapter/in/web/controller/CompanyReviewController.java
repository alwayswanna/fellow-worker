package a.gleb.company_app.adapter.in.web.controller;

import a.gleb.company_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.company_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.company_app.adapter.in.web.controller.docs.CompanyReviewSwagger;
import a.gleb.company_app.adapter.in.web.dto.CompanyReviewRequest;
import a.gleb.company_app.adapter.in.web.dto.CompanyReviewResponse;
import a.gleb.company_app.adapter.in.web.mapper.CompanyReviewWebMapper;
import a.gleb.company_app.application.service.CompanyReviewService;
import a.gleb.fellow_worker.http.response.PageResponse;
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
    private final CompanyReviewWebMapper companyReviewWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyReviewResponse create(
            @PathVariable UUID companyId,
            @Valid @RequestBody CompanyReviewRequest request
    ) {
        return companyReviewWebMapper.toResponse(
                reviewService.create(companyId, request.rating(), request.comment()));
    }

    @Override
    @GetMapping
    public PageResponse<CompanyReviewResponse> findByCompany(
            @PathVariable UUID companyId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var page = reviewService.findByCompany(companyId, PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, companyReviewWebMapper::toResponse);
    }

    @Override
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID companyId, @PathVariable UUID reviewId) {
        reviewService.delete(companyId, reviewId);
    }
}
