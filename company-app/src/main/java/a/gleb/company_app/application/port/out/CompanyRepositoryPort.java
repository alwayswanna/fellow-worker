package a.gleb.company_app.application.port.out;

import a.gleb.company_app.domain.model.Company;
import a.gleb.company_app.domain.model.CompanySearchFilter;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepositoryPort {

    Company create(Company company);

    Company update(Company company);

    Optional<Company> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    boolean existsByOwnerAccountId(UUID ownerAccountId);

    Optional<Company> findByOwnerAccountId(UUID ownerAccountId);

    PageResult<Company> search(CompanySearchFilter filter, PageQuery pageQuery);

    void recalculateRating(UUID id);
}
