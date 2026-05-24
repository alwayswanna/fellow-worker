package a.gleb.resume_app.db.repository;

import a.gleb.resume_app.db.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<SkillEntity, UUID> {

    List<SkillEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
