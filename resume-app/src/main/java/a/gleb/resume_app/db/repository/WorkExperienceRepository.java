package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.WorkExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkExperienceRepository extends JpaRepository<WorkExperienceEntity, UUID> {

    List<WorkExperienceEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
