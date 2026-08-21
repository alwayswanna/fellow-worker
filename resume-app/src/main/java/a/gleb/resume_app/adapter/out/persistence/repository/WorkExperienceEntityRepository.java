package a.gleb.resume_app.adapter.out.persistence.repository;

import a.gleb.resume_app.adapter.out.persistence.entity.WorkExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkExperienceEntityRepository extends JpaRepository<WorkExperienceEntity, UUID> {

    List<WorkExperienceEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
