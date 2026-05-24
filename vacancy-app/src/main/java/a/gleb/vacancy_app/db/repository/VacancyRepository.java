package a.gleb.vacancy_app.db.repository;

import a.gleb.vacancy_app.db.entity.VacancyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface VacancyRepository extends JpaRepository<VacancyEntity, UUID>, JpaSpecificationExecutor<VacancyEntity> {
}
