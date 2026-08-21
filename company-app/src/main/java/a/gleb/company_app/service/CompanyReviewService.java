package a.gleb.company_app.service;

import a.gleb.company_app.db.repository.CompanyRepository;
import a.gleb.company_app.db.repository.CompanyReviewRepository;
import a.gleb.company_app.mapper.CompanyReviewMapper;
import a.gleb.company_app.model.request.CompanyReviewRequest;
import a.gleb.company_app.model.response.CompanyReviewResponse;
import a.gleb.company_app.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyReviewService {

    private final CompanyReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final CompanyReviewMapper mapper;
    private final AccountContext accountContext;

    @Transactional
    public CompanyReviewResponse create(UUID companyId, CompanyReviewRequest request) {
        var accountId = accountContext.requiredAccountId();

        if (reviewRepository.existsByCompanyIdAndAccountId(companyId, accountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reviewed this company");
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        var review = reviewRepository.save(mapper.toEntity(company, accountId, request));
        companyRepository.recalculateRating(companyId);

        return mapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<CompanyReviewResponse> findByCompany(UUID companyId, Pageable pageable) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        return reviewRepository.findAllByCompanyId(companyId, pageable).map(mapper::toResponse);
    }

    @Transactional
    public void delete(UUID companyId, UUID reviewId) {
        var accountId = accountContext.requiredAccountId();
        var review = reviewRepository.findByIdAndAccountId(reviewId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        if (!review.getCompany().getId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
        }

        reviewRepository.deleteById(reviewId);
        companyRepository.recalculateRating(companyId);
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     * Recalculates the rating of every company affected by a removed review.
     */
    @Transactional
    public void deleteAllByAccountId(UUID accountId) {
        var reviews = reviewRepository.findAllByAccountId(accountId);
        if (reviews.isEmpty()) {
            return;
        }

        var affectedCompanyIds = reviews.stream()
                .map(review -> review.getCompany().getId())
                .collect(Collectors.toSet());
        reviewRepository.deleteAll(reviews);
        affectedCompanyIds.forEach(companyRepository::recalculateRating);
    }
}
