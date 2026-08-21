package a.gleb.vacancy_app.application.port.out;

import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.SavedVacancy;

import java.util.Optional;
import java.util.UUID;

public interface SavedVacancyRepositoryPort {

    boolean existsByVacancyIdAndAccountId(UUID vacancyId, UUID accountId);

    Optional<SavedVacancy> findByVacancyIdAndAccountId(UUID vacancyId, UUID accountId);

    SavedVacancy save(SavedVacancy savedVacancy);

    void deleteById(UUID id);

    PageResult<SavedVacancy> findAllByAccountId(UUID accountId, PageQuery pageQuery);

    void deleteAllByAccountId(UUID accountId);
}
