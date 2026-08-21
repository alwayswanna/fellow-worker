package a.gleb.vacancy_app.adapter.out.persistence.repository;

import a.gleb.vacancy_app.adapter.out.persistence.entity.VacancySkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VacancySkillEntityRepository extends JpaRepository<VacancySkillEntity, UUID> {

    @Modifying
    @Query("DELETE FROM VacancySkillEntity s WHERE s.vacancy.id = :vacancyId")
    void deleteAllByVacancyId(@Param("vacancyId") UUID vacancyId);
}
