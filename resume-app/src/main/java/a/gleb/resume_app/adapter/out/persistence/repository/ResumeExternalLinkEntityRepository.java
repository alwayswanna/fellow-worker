package a.gleb.resume_app.adapter.out.persistence.repository;

import a.gleb.resume_app.adapter.out.persistence.entity.ResumeExternalLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeExternalLinkEntityRepository extends JpaRepository<ResumeExternalLinkEntity, UUID> {

    List<ResumeExternalLinkEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
