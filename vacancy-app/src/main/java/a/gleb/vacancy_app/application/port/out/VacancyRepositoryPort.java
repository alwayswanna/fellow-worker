package a.gleb.vacancy_app.application.port.out;

import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.Vacancy;
import a.gleb.vacancy_app.domain.model.VacancySearchFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VacancyRepositoryPort {

    Vacancy create(Vacancy vacancy);

    Vacancy update(Vacancy vacancy);

    Optional<Vacancy> findById(UUID id);

    List<Vacancy> findAllById(List<UUID> ids);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    void deleteAllByCompanyId(UUID companyId);

    PageResult<Vacancy> search(VacancySearchFilter filter, PageQuery pageQuery);
}
