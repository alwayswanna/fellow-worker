package a.gleb.vacancy_app.adapter.out.persistence;

import a.gleb.vacancy_app.adapter.out.persistence.mapper.SavedVacancyPersistenceMapper;
import a.gleb.vacancy_app.adapter.out.persistence.repository.SavedVacancyEntityRepository;
import a.gleb.vacancy_app.application.port.out.SavedVacancyRepositoryPort;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.SavedVacancy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SavedVacancyRepositoryAdapter implements SavedVacancyRepositoryPort {

    private final SavedVacancyEntityRepository savedVacancyEntityRepository;
    private final SavedVacancyPersistenceMapper savedVacancyPersistenceMapper;

    @Override
    public boolean existsByVacancyIdAndAccountId(UUID vacancyId, UUID accountId) {
        return savedVacancyEntityRepository.existsByVacancyIdAndAccountId(vacancyId, accountId);
    }

    @Override
    public Optional<SavedVacancy> findByVacancyIdAndAccountId(UUID vacancyId, UUID accountId) {
        return savedVacancyEntityRepository.findByVacancyIdAndAccountId(vacancyId, accountId)
                .map(savedVacancyPersistenceMapper::toDomain);
    }

    @Override
    public SavedVacancy save(SavedVacancy savedVacancy) {
        var saved = savedVacancyEntityRepository.save(savedVacancyPersistenceMapper.toEntity(savedVacancy));
        return savedVacancyPersistenceMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        savedVacancyEntityRepository.deleteById(id);
    }

    @Override
    public PageResult<SavedVacancy> findAllByAccountId(UUID accountId, PageQuery pageQuery) {
        var page = savedVacancyEntityRepository.findAllByAccountId(accountId, PersistencePageSupport.toPageable(pageQuery));
        return PersistencePageSupport.toPageResult(page, savedVacancyPersistenceMapper::toDomain);
    }

    @Override
    public void deleteAllByAccountId(UUID accountId) {
        savedVacancyEntityRepository.deleteAllByAccountId(accountId);
    }
}
