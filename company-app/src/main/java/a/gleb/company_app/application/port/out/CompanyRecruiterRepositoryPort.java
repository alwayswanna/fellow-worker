package a.gleb.company_app.application.port.out;

import a.gleb.company_app.domain.model.CompanyRecruiter;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRecruiterRepositoryPort {

    CompanyRecruiter save(CompanyRecruiter recruiter);

    PageResult<CompanyRecruiter> findAllByCompanyId(UUID companyId, PageQuery pageQuery);

    Optional<CompanyRecruiter> findByCompanyIdAndAccountId(UUID companyId, UUID accountId);

    boolean existsByCompanyId(UUID companyId);

    boolean existsByAccountId(UUID accountId);

    Optional<CompanyRecruiter> findByAccountId(UUID accountId);

    void deleteById(UUID id);

    void deleteByAccountId(UUID accountId);
}
