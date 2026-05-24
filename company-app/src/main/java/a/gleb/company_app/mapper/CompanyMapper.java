package a.gleb.company_app.mapper;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.company_app.model.request.CompanyRequest;
import a.gleb.company_app.model.response.CompanyResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CompanyMapper {

    public CompanyEntity toEntity(UUID ownerAccountId, CompanyRequest request) {
        return CompanyEntity.builder()
                .ownerAccountId(ownerAccountId)
                .name(request.name())
                .description(request.description())
                .website(request.website())
                .industry(request.industry())
                .size(request.size())
                .city(request.city())
                .country(request.country())
                .rating(0.0)
                .reviewCount(0)
                .build();
    }

    public CompanyEntity toEntity(CompanyEntity existing, CompanyRequest request) {
        return CompanyEntity.builder()
                .id(existing.getId())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .ownerAccountId(existing.getOwnerAccountId())
                .logoUrl(existing.getLogoUrl())
                .rating(existing.getRating())
                .reviewCount(existing.getReviewCount())
                .name(request.name())
                .description(request.description())
                .website(request.website())
                .industry(request.industry())
                .size(request.size())
                .city(request.city())
                .country(request.country())
                .build();
    }

    public CompanyResponse toResponse(CompanyEntity entity) {
        return CompanyResponse.builder()
                .id(entity.getId())
                .ownerAccountId(entity.getOwnerAccountId())
                .name(entity.getName())
                .description(entity.getDescription())
                .website(entity.getWebsite())
                .logoUrl(entity.getLogoUrl())
                .industry(entity.getIndustry())
                .size(entity.getSize())
                .city(entity.getCity())
                .country(entity.getCountry())
                .rating(entity.getRating())
                .reviewCount(entity.getReviewCount())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
