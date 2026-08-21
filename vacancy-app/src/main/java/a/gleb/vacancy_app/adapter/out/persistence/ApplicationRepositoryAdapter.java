package a.gleb.vacancy_app.adapter.out.persistence;

import a.gleb.vacancy_app.adapter.out.persistence.mapper.ApplicationPersistenceMapper;
import a.gleb.vacancy_app.adapter.out.persistence.repository.ApplicationEntityRepository;
import a.gleb.vacancy_app.application.port.out.ApplicationRepositoryPort;
import a.gleb.vacancy_app.domain.model.Application;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApplicationRepositoryAdapter implements ApplicationRepositoryPort {

    private final ApplicationEntityRepository applicationEntityRepository;
    private final ApplicationPersistenceMapper applicationPersistenceMapper;

    @Override
    public Optional<Application> findByVacancyIdAndApplicantAccountId(UUID vacancyId, UUID applicantAccountId) {
        return applicationEntityRepository.findByVacancyIdAndApplicantAccountId(vacancyId, applicantAccountId)
                .map(applicationPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Application> findById(UUID id) {
        return applicationEntityRepository.findById(id).map(applicationPersistenceMapper::toDomain);
    }

    @Override
    public Application save(Application application) {
        var saved = applicationEntityRepository.save(applicationPersistenceMapper.toEntity(application));
        return applicationPersistenceMapper.toDomain(saved);
    }

    @Override
    public PageResult<Application> findAllByVacancyId(UUID vacancyId, PageQuery pageQuery) {
        var page = applicationEntityRepository.findAllByVacancyId(vacancyId, PersistencePageSupport.toPageable(pageQuery));
        return PersistencePageSupport.toPageResult(page, applicationPersistenceMapper::toDomain);
    }

    @Override
    public List<Application> findAllByApplicantAccountId(UUID applicantAccountId) {
        return applicationEntityRepository.findAllByApplicantAccountId(applicantAccountId).stream()
                .map(applicationPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByApplicantAccountId(UUID accountId) {
        applicationEntityRepository.deleteAllByApplicantAccountId(accountId);
    }
}
