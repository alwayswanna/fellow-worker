package a.gleb.resume_app.adapter.out.persistence.repository;

import a.gleb.resume_app.adapter.out.persistence.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeEntityRepository extends JpaRepository<ResumeEntity, UUID> {

    /**
     * Capped rather than a true {@code findAllByAccountId} — this backs {@code /my} and
     * {@code ?accountId=} lookups, which return a plain, unpaginated list; the cap is a safety net
     * against an account somehow accumulating an unbounded number of resumes, not an expected limit.
     */
    List<ResumeEntity> findTop100ByAccountId(UUID accountId);

    Optional<ResumeEntity> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByAccountId(UUID accountId);

    void deleteByIdAndAccountId(UUID id, UUID accountId);

    /**
     * Bulk delete — DB-level `ON DELETE CASCADE` on resume child tables (work_experience,
     * education, skill, resume_link) takes care of dependent rows.
     */
    @Modifying
    @Query("DELETE FROM ResumeEntity r WHERE r.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
}
