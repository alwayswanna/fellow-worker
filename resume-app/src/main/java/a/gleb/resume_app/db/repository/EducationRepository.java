package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<EducationEntity, UUID> {

    List<EducationEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
