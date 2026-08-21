package a.gleb.company_app.application.service;

import a.gleb.company_app.application.port.out.CompanyRepositoryPort;
import a.gleb.company_app.application.port.out.CompanyReviewRepositoryPort;
import a.gleb.company_app.application.port.out.CurrentAccountPort;
import a.gleb.company_app.domain.exception.AlreadyReviewedException;
import a.gleb.company_app.domain.exception.CompanyNotFoundException;
import a.gleb.company_app.domain.exception.ReviewNotFoundException;
import a.gleb.company_app.domain.model.CompanyReview;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyReviewService {

    private final CompanyReviewRepositoryPort companyReviewRepositoryPort;
    private final CompanyRepositoryPort companyRepositoryPort;
    private final CurrentAccountPort currentAccountPort;

    @Transactional
    public CompanyReview create(UUID companyId, int rating, String comment) {
        var accountId = currentAccountPort.requiredAccountId();

        if (companyReviewRepositoryPort.existsByCompanyIdAndAccountId(companyId, accountId)) {
            throw new AlreadyReviewedException("You have already reviewed this company");
        }

        if (!companyRepositoryPort.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        var review = CompanyReview.builder()
                .companyId(companyId)
                .accountId(accountId)
                .rating(rating)
                .comment(comment)
                .build();
        var saved = companyReviewRepositoryPort.save(review);
        companyRepositoryPort.recalculateRating(companyId);

        return saved;
    }

    @Transactional(readOnly = true)
    public PageResult<CompanyReview> findByCompany(UUID companyId, PageQuery pageQuery) {
        if (!companyRepositoryPort.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return companyReviewRepositoryPort.findAllByCompanyId(companyId, pageQuery);
    }

    @Transactional
    public void delete(UUID companyId, UUID reviewId) {
        var accountId = currentAccountPort.requiredAccountId();
        var review = companyReviewRepositoryPort.findByIdAndAccountId(reviewId, accountId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getCompanyId().equals(companyId)) {
            throw new ReviewNotFoundException("Review not found");
        }

        companyReviewRepositoryPort.deleteById(reviewId);
        companyRepositoryPort.recalculateRating(companyId);
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     * Recalculates the rating of every company affected by a removed review.
     */
    @Transactional
    public void deleteAllByAccountId(UUID accountId) {
        var reviews = companyReviewRepositoryPort.findAllByAccountId(accountId);
        if (reviews.isEmpty()) {
            return;
        }

        var affectedCompanyIds = reviews.stream()
                .map(CompanyReview::getCompanyId)
                .collect(Collectors.toSet());
        companyReviewRepositoryPort.deleteAll(reviews);
        affectedCompanyIds.forEach(companyRepositoryPort::recalculateRating);
    }
}
