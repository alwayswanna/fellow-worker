package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.ResumeExternalLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeExternalLinkRepository extends JpaRepository<ResumeExternalLinkEntity, UUID> {

    List<ResumeExternalLinkEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
