package a.gleb.vacancy_app.db.repository;

import a.gleb.vacancy_app.db.entity.VacancyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VacancyRepository extends JpaRepository<VacancyEntity, UUID>, JpaSpecificationExecutor<VacancyEntity> {

    /**
     * Bulk delete — DB-level `ON DELETE CASCADE` on vacancy_skill/application takes care of
     * dependent rows.
     */
    @Modifying
    @Query("DELETE FROM VacancyEntity v WHERE v.companyId = :companyId")
    void deleteAllByCompanyId(@Param("companyId") UUID companyId);
}
