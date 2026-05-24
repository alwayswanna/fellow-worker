package a.gleb.company_app.db.repository;

import a.gleb.company_app.db.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID>, JpaSpecificationExecutor<CompanyEntity> {

    boolean existsByOwnerAccountId(UUID ownerAccountId);

    java.util.Optional<CompanyEntity> findByOwnerAccountId(UUID ownerAccountId);

    @Modifying
    @Query("""
            UPDATE CompanyEntity c
            SET c.rating      = (SELECT COALESCE(AVG(CAST(r.rating AS double)), 0.0) FROM CompanyReviewEntity r WHERE r.company.id = :id),
                c.reviewCount = (SELECT COUNT(r) FROM CompanyReviewEntity r WHERE r.company.id = :id)
            WHERE c.id = :id
            """)
    void recalculateRating(@Param("id") UUID id);
}
