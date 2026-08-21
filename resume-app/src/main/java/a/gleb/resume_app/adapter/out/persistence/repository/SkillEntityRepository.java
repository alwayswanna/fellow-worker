package a.gleb.resume_app.adapter.out.persistence.repository;

import a.gleb.resume_app.adapter.out.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillEntityRepository extends JpaRepository<SkillEntity, UUID> {

    List<SkillEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
