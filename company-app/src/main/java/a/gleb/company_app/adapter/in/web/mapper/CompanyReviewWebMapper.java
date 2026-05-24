package a.gleb.company_app.adapter.in.web.mapper;

import a.gleb.company_app.adapter.in.web.dto.CompanyReviewResponse;
import a.gleb.company_app.domain.model.CompanyReview;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class CompanyReviewWebMapper {

    public CompanyReviewResponse toResponse(CompanyReview review) {
        return CompanyReviewResponse.builder()
                .id(review.getId())
                .companyId(review.getCompanyId())
                .accountId(review.getAccountId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
