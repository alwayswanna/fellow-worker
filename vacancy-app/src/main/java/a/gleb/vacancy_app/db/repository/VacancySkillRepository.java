package a.gleb.vacancy_app.db.repository;

import a.gleb.vacancy_app.db.entity.VacancySkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VacancySkillRepository extends JpaRepository<VacancySkillEntity, UUID> {

    @Modifying
    @Query("DELETE FROM VacancySkillEntity s WHERE s.vacancy.id = :vacancyId")
    void deleteAllByVacancyId(@Param("vacancyId") UUID vacancyId);
}
