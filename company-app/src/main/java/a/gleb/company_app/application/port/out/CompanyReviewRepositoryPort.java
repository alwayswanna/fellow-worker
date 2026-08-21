package a.gleb.company_app.application.port.out;

import a.gleb.company_app.domain.model.CompanyReview;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyReviewRepositoryPort {

    CompanyReview save(CompanyReview review);

    PageResult<CompanyReview> findAllByCompanyId(UUID companyId, PageQuery pageQuery);

    Optional<CompanyReview> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByCompanyIdAndAccountId(UUID companyId, UUID accountId);

    List<CompanyReview> findAllByAccountId(UUID accountId);

    void deleteById(UUID id);

    void deleteAll(List<CompanyReview> reviews);
}
