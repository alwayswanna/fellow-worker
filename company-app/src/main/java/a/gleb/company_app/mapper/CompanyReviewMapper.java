package a.gleb.company_app.mapper;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.company_app.db.entity.CompanyReviewEntity;
import a.gleb.company_app.model.request.CompanyReviewRequest;
import a.gleb.company_app.model.response.CompanyReviewResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CompanyReviewMapper {

    public CompanyReviewEntity toEntity(CompanyEntity company, UUID accountId, CompanyReviewRequest request) {
        return CompanyReviewEntity.builder()
                .company(company)
                .accountId(accountId)
                .rating(request.rating())
                .comment(request.comment())
                .build();
    }

    public CompanyReviewResponse toResponse(CompanyReviewEntity entity) {
        return CompanyReviewResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompany().getId())
                .accountId(entity.getAccountId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
