package a.gleb.company_app.application.service;

import a.gleb.company_app.application.port.out.CompanyRecruiterRepositoryPort;
import a.gleb.company_app.application.port.out.CompanyRepositoryPort;
import a.gleb.company_app.application.port.out.CurrentAccountPort;
import a.gleb.company_app.domain.exception.AlreadyOwnCompanyException;
import a.gleb.company_app.domain.exception.AlreadyRecruiterElsewhereException;
import a.gleb.company_app.domain.exception.CompanyHasRecruitersException;
import a.gleb.company_app.domain.exception.CompanyNotFoundException;
import a.gleb.company_app.domain.exception.NotCompanyOwnerException;
import a.gleb.company_app.domain.model.Company;
import a.gleb.company_app.domain.model.CompanySearchFilter;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepositoryPort companyRepositoryPort;
    private final CompanyRecruiterRepositoryPort companyRecruiterRepositoryPort;
    private final CurrentAccountPort currentAccountPort;
    private final OutboxEventService outboxEventService;

    @Transactional
    public Company create(Company company) {
        var currentAccountId = currentAccountPort.requiredAccountId();

        if (companyRepositoryPort.existsByOwnerAccountId(currentAccountId)) {
            throw new AlreadyOwnCompanyException("You already own a company");
        }
        if (companyRecruiterRepositoryPort.existsByAccountId(currentAccountId)) {
            throw new AlreadyRecruiterElsewhereException("You are already a recruiter at another company");
        }

        company.setOwnerAccountId(currentAccountId);
        company.setRating(0.0);
        company.setReviewCount(0);
        return companyRepositoryPort.create(company);
    }

    @Transactional(readOnly = true)
    public Company findById(UUID id) {
        return companyRepositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Company> findMyCompany() {
        var currentAccountId = currentAccountPort.requiredAccountId();
        var asOwner = companyRepositoryPort.findByOwnerAccountId(currentAccountId);
        if (asOwner.isPresent()) {
            return asOwner;
        }
        return companyRecruiterRepositoryPort.findByAccountId(currentAccountId)
                .flatMap(recruiter -> companyRepositoryPort.findById(recruiter.getCompanyId()));
    }

    @Transactional(readOnly = true)
    public PageResult<Company> findAll(CompanySearchFilter filter, PageQuery pageQuery) {
        return companyRepositoryPort.search(filter, pageQuery);
    }

    @Transactional
    public Company update(UUID id, Company incoming) {
        var currentAccountId = currentAccountPort.requiredAccountId();
        var existing = companyRepositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        if (!existing.getOwnerAccountId().equals(currentAccountId)) {
            throw new NotCompanyOwnerException("Only the company owner can update it");
        }

        incoming.setId(existing.getId());
        incoming.setCreatedAt(existing.getCreatedAt());
        incoming.setCreatedBy(existing.getCreatedBy());
        incoming.setOwnerAccountId(existing.getOwnerAccountId());
        incoming.setLogoUrl(existing.getLogoUrl());
        incoming.setRating(existing.getRating());
        incoming.setReviewCount(existing.getReviewCount());

        return companyRepositoryPort.update(incoming);
    }

    @Transactional
    public void delete(UUID id) {
        var currentAccountId = currentAccountPort.requiredAccountId();
        var company = companyRepositoryPort.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new NotCompanyOwnerException("Only the company owner can delete it");
        }
        if (companyRecruiterRepositoryPort.existsByCompanyId(id)) {
            throw new CompanyHasRecruitersException(
                    "Cannot delete company with active recruiters. Remove all recruiters first");
        }

        // TODO: check that company has no active vacancies in vacancy-app (cross-service call)

        companyRepositoryPort.deleteById(id);
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     * If the account owns a company, it's deleted (DB `ON DELETE CASCADE` removes its remaining
     * recruiters/reviews) and a `COMPANY_DELETED` outbox event is emitted so vacancy-app can drop
     * the company's vacancies/applications in turn.
     */
    @Transactional
    public void deleteOwnedByAccount(UUID accountId) {
        companyRepositoryPort.findByOwnerAccountId(accountId).ifPresent(company -> {
            outboxEventService.saveEvent(company, CompanyEventType.COMPANY_DELETED);
            companyRepositoryPort.deleteById(company.getId());
            log.info("CompanyService: deleted company owned by removed account [companyId={}, userId={}]",
                    company.getId(), accountId);
        });
    }
}
