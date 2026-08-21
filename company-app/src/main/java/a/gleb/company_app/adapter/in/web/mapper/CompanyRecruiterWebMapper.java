package a.gleb.company_app.adapter.in.web.mapper;

import a.gleb.company_app.adapter.in.web.dto.CompanyRecruiterResponse;
import a.gleb.company_app.domain.model.CompanyRecruiter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class CompanyRecruiterWebMapper {

    public CompanyRecruiterResponse toResponse(CompanyRecruiter recruiter) {
        return CompanyRecruiterResponse.builder()
                .id(recruiter.getId())
                .companyId(recruiter.getCompanyId())
                .accountId(recruiter.getAccountId())
                .joinedAt(recruiter.getCreatedAt() != null ? recruiter.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
