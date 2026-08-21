package a.gleb.company_app.adapter.out.persistence.mapper;

import a.gleb.company_app.adapter.out.persistence.entity.CompanyEntity;
import a.gleb.company_app.adapter.out.persistence.entity.CompanyReviewEntity;
import a.gleb.company_app.domain.model.CompanyReview;
import org.springframework.stereotype.Component;

@Component
public class CompanyReviewPersistenceMapper {

    public CompanyReviewEntity toEntity(CompanyReview review, CompanyEntity companyRef) {
        return CompanyReviewEntity.builder()
                .id(review.getId())
                .company(companyRef)
                .accountId(review.getAccountId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public CompanyReview toDomain(CompanyReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        return CompanyReview.builder()
                .id(entity.getId())
                .companyId(entity.getCompany().getId())
                .accountId(entity.getAccountId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
