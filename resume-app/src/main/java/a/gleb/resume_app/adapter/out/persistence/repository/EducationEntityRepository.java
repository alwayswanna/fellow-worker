package a.gleb.resume_app.adapter.out.persistence.repository;

import a.gleb.resume_app.adapter.out.persistence.entity.EducationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationEntityRepository extends JpaRepository<EducationEntity, UUID> {

    List<EducationEntity> findAllByResumeId(UUID resumeId);

    void deleteAllByResumeId(UUID resumeId);
}
