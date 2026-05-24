package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<ResumeEntity, UUID> {

    List<ResumeEntity> findAllByAccountId(UUID accountId);

    Optional<ResumeEntity> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByIdAndAccountId(UUID id, UUID accountId);

    void deleteByIdAndAccountId(UUID id, UUID accountId);
}
