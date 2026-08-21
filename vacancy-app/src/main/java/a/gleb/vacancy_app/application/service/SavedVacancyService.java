package a.gleb.vacancy_app.application.service;

import a.gleb.vacancy_app.application.port.out.CurrentAccountPort;
import a.gleb.vacancy_app.application.port.out.SavedVacancyRepositoryPort;
import a.gleb.vacancy_app.application.port.out.VacancyRepositoryPort;
import a.gleb.vacancy_app.domain.exception.VacancyAlreadySavedException;
import a.gleb.vacancy_app.domain.exception.VacancyNotFoundException;
import a.gleb.vacancy_app.domain.exception.VacancyNotSavedException;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.SavedVacancy;
import a.gleb.vacancy_app.domain.model.Vacancy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedVacancyService {

    private final SavedVacancyRepositoryPort savedVacancyRepositoryPort;
    private final VacancyRepositoryPort vacancyRepositoryPort;
    private final CurrentAccountPort currentAccountPort;

    @Transactional
    public void save(UUID vacancyId) {
        var accountId = currentAccountPort.requiredAccountId();

        if (savedVacancyRepositoryPort.existsByVacancyIdAndAccountId(vacancyId, accountId)) {
            throw new VacancyAlreadySavedException("Vacancy already saved");
        }
        if (!vacancyRepositoryPort.existsById(vacancyId)) {
            throw new VacancyNotFoundException("Vacancy not found");
        }

        savedVacancyRepositoryPort.save(SavedVacancy.builder()
                .vacancyId(vacancyId)
                .accountId(accountId)
                .build());
    }

    @Transactional
    public void unsave(UUID vacancyId) {
        var accountId = currentAccountPort.requiredAccountId();
        var saved = savedVacancyRepositoryPort.findByVacancyIdAndAccountId(vacancyId, accountId)
                .orElseThrow(() -> new VacancyNotSavedException("Vacancy is not saved"));
        savedVacancyRepositoryPort.deleteById(saved.getId());
    }

    @Transactional(readOnly = true)
    public PageResult<Vacancy> getSaved(PageQuery pageQuery) {
        var accountId = currentAccountPort.requiredAccountId();
        var savedPage = savedVacancyRepositoryPort.findAllByAccountId(accountId, pageQuery);

        var vacancyIds = savedPage.content().stream().map(SavedVacancy::getVacancyId).toList();
        var vacanciesById = vacancyRepositoryPort.findAllById(vacancyIds).stream()
                .collect(Collectors.toMap(Vacancy::getId, v -> v));

        var content = vacancyIds.stream()
                .map(vacanciesById::get)
                .filter(Objects::nonNull)
                .toList();

        return new PageResult<>(content, savedPage.page(), savedPage.size(), savedPage.totalElements(), savedPage.totalPages());
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     */
    @Transactional
    public void deleteAllForAccount(UUID accountId) {
        savedVacancyRepositoryPort.deleteAllByAccountId(accountId);
        log.info("SavedVacancyService: deleted saved vacancies for removed account [userId={}]", accountId);
    }
}
