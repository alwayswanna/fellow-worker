package a.gleb.vacancy_app.application.port.out;

import a.gleb.vacancy_app.domain.model.Application;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepositoryPort {

    Optional<Application> findByVacancyIdAndApplicantAccountId(UUID vacancyId, UUID applicantAccountId);

    Optional<Application> findById(UUID id);

    Application save(Application application);

    PageResult<Application> findAllByVacancyId(UUID vacancyId, PageQuery pageQuery);

    List<Application> findAllByApplicantAccountId(UUID applicantAccountId);

    void deleteAllByApplicantAccountId(UUID accountId);
}
