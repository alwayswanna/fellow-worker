package a.gleb.company_app.mapper;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.company_app.db.entity.CompanyRecruiterEntity;
import a.gleb.company_app.model.response.CompanyRecruiterResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CompanyRecruiterMapper {

    public CompanyRecruiterEntity toEntity(CompanyEntity company, UUID accountId) {
        return CompanyRecruiterEntity.builder()
                .company(company)
                .accountId(accountId)
                .build();
    }

    public CompanyRecruiterResponse toResponse(CompanyRecruiterEntity entity) {
        return CompanyRecruiterResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompany().getId())
                .accountId(entity.getAccountId())
                .joinedAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
