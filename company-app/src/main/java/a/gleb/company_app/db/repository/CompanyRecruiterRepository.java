package a.gleb.company_app.db.repository;

import a.gleb.company_app.db.entity.CompanyRecruiterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRecruiterRepository extends JpaRepository<CompanyRecruiterEntity, UUID> {

    Page<CompanyRecruiterEntity> findAllByCompanyId(UUID companyId, Pageable pageable);

    Optional<CompanyRecruiterEntity> findByCompanyIdAndAccountId(UUID companyId, UUID accountId);

    boolean existsByCompanyId(UUID companyId);

    boolean existsByAccountId(UUID accountId);

    Optional<CompanyRecruiterEntity> findByAccountId(UUID accountId);

    void deleteByAccountId(UUID accountId);
}
