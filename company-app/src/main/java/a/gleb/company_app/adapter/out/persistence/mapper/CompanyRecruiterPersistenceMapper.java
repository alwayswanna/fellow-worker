package a.gleb.company_app.adapter.out.persistence.mapper;

import a.gleb.company_app.adapter.out.persistence.entity.CompanyEntity;
import a.gleb.company_app.adapter.out.persistence.entity.CompanyRecruiterEntity;
import a.gleb.company_app.domain.model.CompanyRecruiter;
import org.springframework.stereotype.Component;

@Component
public class CompanyRecruiterPersistenceMapper {

    public CompanyRecruiterEntity toEntity(CompanyRecruiter recruiter, CompanyEntity companyRef) {
        return CompanyRecruiterEntity.builder()
                .id(recruiter.getId())
                .company(companyRef)
                .accountId(recruiter.getAccountId())
                .createdAt(recruiter.getCreatedAt())
                .build();
    }

    public CompanyRecruiter toDomain(CompanyRecruiterEntity entity) {
        if (entity == null) {
            return null;
        }
        return CompanyRecruiter.builder()
                .id(entity.getId())
                .companyId(entity.getCompany().getId())
                .accountId(entity.getAccountId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
