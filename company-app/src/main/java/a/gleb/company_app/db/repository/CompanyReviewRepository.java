package a.gleb.company_app.db.repository;

import a.gleb.company_app.db.entity.CompanyReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyReviewRepository extends JpaRepository<CompanyReviewEntity, UUID> {

    Page<CompanyReviewEntity> findAllByCompanyId(UUID companyId, Pageable pageable);

    Optional<CompanyReviewEntity> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByCompanyIdAndAccountId(UUID companyId, UUID accountId);
}
