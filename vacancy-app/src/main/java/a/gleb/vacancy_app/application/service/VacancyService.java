package a.gleb.vacancy_app.application.service;

import a.gleb.vacancy_app.application.port.out.VacancyRepositoryPort;
import a.gleb.vacancy_app.domain.exception.VacancyNotFoundException;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.Vacancy;
import a.gleb.vacancy_app.domain.model.VacancySearchFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepositoryPort vacancyRepositoryPort;
    private final CompanyAccessGuard companyAccessGuard;

    @Transactional
    public Vacancy create(Vacancy vacancy) {
        companyAccessGuard.assertCanManage(vacancy.getCompanyId(), "vacancies");
        return vacancyRepositoryPort.create(vacancy);
    }

    @Transactional(readOnly = true)
    public Vacancy findById(UUID id) {
        return vacancyRepositoryPort.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<Vacancy> search(VacancySearchFilter filter, PageQuery pageQuery) {
        return vacancyRepositoryPort.search(filter, pageQuery);
    }

    @Transactional
    public Vacancy update(UUID id, Vacancy incoming) {
        var existing = vacancyRepositoryPort.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));
        companyAccessGuard.assertCanManage(existing.getCompanyId(), "vacancies");

        incoming.setId(existing.getId());
        // Company ownership of a vacancy isn't transferable via update — always keep the
        // original, regardless of what the request body sends.
        incoming.setCompanyId(existing.getCompanyId());
        incoming.setCreatedAt(existing.getCreatedAt());
        incoming.setCreatedBy(existing.getCreatedBy());

        return vacancyRepositoryPort.update(incoming);
    }

    @Transactional
    public void delete(UUID id) {
        var existing = vacancyRepositoryPort.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));
        companyAccessGuard.assertCanManage(existing.getCompanyId(), "vacancies");
        vacancyRepositoryPort.deleteById(id);
    }

    /**
     * Called when company-app reports the company was deleted (`COMPANY_DELETED` event).
     */
    @Transactional
    public void deleteAllForCompany(UUID companyId) {
        vacancyRepositoryPort.deleteAllByCompanyId(companyId);
        log.info("VacancyService: deleted vacancies for removed company [companyId={}]", companyId);
    }
}
