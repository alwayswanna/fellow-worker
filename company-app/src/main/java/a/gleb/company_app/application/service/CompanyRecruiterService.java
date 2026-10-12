package a.gleb.company_app.application.service;

import a.gleb.company_app.application.port.out.CompanyRecruiterRepositoryPort;
import a.gleb.company_app.application.port.out.CompanyRepositoryPort;
import a.gleb.company_app.application.port.out.CurrentAccountPort;
import a.gleb.company_app.domain.exception.AlreadyCompanyOwnerElsewhereException;
import a.gleb.company_app.domain.exception.AlreadyRecruiterElsewhereException;
import a.gleb.company_app.domain.exception.CompanyNotFoundException;
import a.gleb.company_app.domain.exception.NotCompanyOwnerException;
import a.gleb.company_app.domain.exception.OwnerCannotBeRecruiterException;
import a.gleb.company_app.domain.exception.RecruiterNotFoundException;
import a.gleb.company_app.domain.model.CompanyRecruiter;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyRecruiterService {

    private final CompanyRecruiterRepositoryPort companyRecruiterRepositoryPort;
    private final CompanyRepositoryPort companyRepositoryPort;
    private final CurrentAccountPort currentAccountPort;

    @Transactional
    public CompanyRecruiter addRecruiter(UUID companyId, UUID recruiterAccountId) {
        var currentAccountId = currentAccountPort.requiredAccountId();
        var company = companyRepositoryPort.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new NotCompanyOwnerException("Only the company owner can add recruiters");
        }

        if (recruiterAccountId.equals(company.getOwnerAccountId())) {
            throw new OwnerCannotBeRecruiterException("Owner cannot be added as a recruiter");
        }
        if (companyRepositoryPort.existsByOwnerAccountId(recruiterAccountId)) {
            throw new AlreadyCompanyOwnerElsewhereException("This account is already an owner of another company");
        }
        if (companyRecruiterRepositoryPort.existsByAccountId(recruiterAccountId)) {
            throw new AlreadyRecruiterElsewhereException("This account is already a recruiter at another company");
        }

        var recruiter = CompanyRecruiter.builder()
                .companyId(companyId)
                .accountId(recruiterAccountId)
                .build();
        return companyRecruiterRepositoryPort.save(recruiter);
    }

    /**
     * Internal, service-to-service existence check (e.g. vacancy-app verifying that the caller
     * managing a vacancy is a recruiter at its owning company) — no account-level details leak,
     * just a boolean, so it doesn't need the current-account/owner checks the methods below have.
     */
    @Transactional(readOnly = true)
    public boolean exists(UUID companyId, UUID accountId) {
        return companyRecruiterRepositoryPort.findByCompanyIdAndAccountId(companyId, accountId).isPresent();
    }

    @Transactional(readOnly = true)
    public PageResult<CompanyRecruiter> findByCompany(UUID companyId, PageQuery pageQuery) {
        if (!companyRepositoryPort.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return companyRecruiterRepositoryPort.findAllByCompanyId(companyId, pageQuery);
    }

    @Transactional
    public void removeRecruiter(UUID companyId, UUID recruiterAccountId) {
        var currentAccountId = currentAccountPort.requiredAccountId();
        var company = companyRepositoryPort.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new NotCompanyOwnerException("Only the company owner can remove recruiters");
        }

        var recruiter = companyRecruiterRepositoryPort.findByCompanyIdAndAccountId(companyId, recruiterAccountId)
                .orElseThrow(() -> new RecruiterNotFoundException("Recruiter not found in this company"));

        companyRecruiterRepositoryPort.deleteById(recruiter.getId());
    }

    @Transactional
    public void removeSelf(UUID companyId) {
        var currentAccountId = currentAccountPort.requiredAccountId();

        if (!companyRepositoryPort.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }

        var recruiter = companyRecruiterRepositoryPort.findByCompanyIdAndAccountId(companyId, currentAccountId)
                .orElseThrow(() -> new RecruiterNotFoundException("You are not a recruiter at this company"));

        // TODO: check that this recruiter has no active vacancies in vacancy-app before allowing self-removal (cross-service call)

        companyRecruiterRepositoryPort.deleteById(recruiter.getId());
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     */
    @Transactional
    public void deleteByAccountId(UUID accountId) {
        companyRecruiterRepositoryPort.deleteByAccountId(accountId);
    }
}
