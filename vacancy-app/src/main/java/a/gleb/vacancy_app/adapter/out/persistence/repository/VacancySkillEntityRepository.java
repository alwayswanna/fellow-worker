package a.gleb.vacancy_app.adapter.out.persistence.repository;

import a.gleb.vacancy_app.adapter.out.persistence.entity.VacancySkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VacancySkillEntityRepository extends JpaRepository<VacancySkillEntity, UUID> {

    // clearAutomatically evicts the persistence context after this bulk delete, so the
    // VacancyEntity.skills collection the caller already loaded in the same transaction
    // (e.g. VacancyService.update's `findById`) isn't left stale and merged back in on save,
    // which Hibernate would otherwise try to update/delete rows already gone — surfacing as a
    // false "modified concurrently" (ObjectOptimisticLockingFailureException) on every edit.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM VacancySkillEntity s WHERE s.vacancy.id = :vacancyId")
    void deleteAllByVacancyId(@Param("vacancyId") UUID vacancyId);
}
