package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<ResumeEntity, UUID> {

    List<ResumeEntity> findAllByAccountId(UUID accountId);

    Optional<ResumeEntity> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByIdAndAccountId(UUID id, UUID accountId);

    void deleteByIdAndAccountId(UUID id, UUID accountId);

    /**
     * Bulk delete — DB-level `ON DELETE CASCADE` on resume child tables (work_experience,
     * education, skill, resume_link) takes care of dependent rows.
     */
    @Modifying
    @Query("DELETE FROM ResumeEntity r WHERE r.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
}
