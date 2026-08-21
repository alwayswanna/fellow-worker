package a.gleb.company_app.adapter.out.persistence.mapper;

import a.gleb.company_app.adapter.out.persistence.entity.CompanyEntity;
import a.gleb.company_app.domain.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyPersistenceMapper {

    public CompanyEntity toEntity(Company company) {
        if (company == null) {
            return null;
        }
        return CompanyEntity.builder()
                .id(company.getId())
                .ownerAccountId(company.getOwnerAccountId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .logoUrl(company.getLogoUrl())
                .industry(company.getIndustry())
                .size(company.getSize())
                .city(company.getCity())
                .country(company.getCountry())
                .rating(company.getRating())
                .reviewCount(company.getReviewCount())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .createdBy(company.getCreatedBy())
                .updatedBy(company.getUpdatedBy())
                .build();
    }

    public Company toDomain(CompanyEntity entity) {
        if (entity == null) {
            return null;
        }
        return Company.builder()
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
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
