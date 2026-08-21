package a.gleb.company_app.adapter.in.web.mapper;

import a.gleb.company_app.adapter.in.web.dto.CompanyRequest;
import a.gleb.company_app.adapter.in.web.dto.CompanyResponse;
import a.gleb.company_app.domain.model.Company;
import a.gleb.company_app.domain.model.CompanySearchFilter;
import a.gleb.company_app.domain.model.enums.CompanySize;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class CompanyWebMapper {

    public Company toDomain(CompanyRequest request) {
        return Company.builder()
                .name(request.name())
                .description(request.description())
                .website(request.website())
                .industry(request.industry())
                .size(request.size())
                .city(request.city())
                .country(request.country())
                .build();
    }

    public CompanySearchFilter toFilter(String name, String industry, String city, CompanySize size) {
        return new CompanySearchFilter(name, industry, city, size);
    }

    public CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
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
                .createdAt(company.getCreatedAt() != null ? company.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(company.getUpdatedAt() != null ? company.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
