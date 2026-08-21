package a.gleb.company_app.service;

import a.gleb.company_app.db.repository.CompanyRecruiterRepository;
import a.gleb.company_app.db.repository.CompanyRepository;
import a.gleb.company_app.mapper.CompanyRecruiterMapper;
import a.gleb.company_app.model.request.AddRecruiterRequest;
import a.gleb.company_app.model.response.CompanyRecruiterResponse;
import a.gleb.company_app.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyRecruiterService {

    private final CompanyRecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final CompanyRecruiterMapper mapper;
    private final AccountContext accountContext;

    @Transactional
    public CompanyRecruiterResponse addRecruiter(UUID companyId, AddRecruiterRequest request) {
        var currentAccountId = accountContext.requiredAccountId();
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the company owner can add recruiters");
        }

        var recruiterAccountId = request.accountId();

        if (recruiterAccountId.equals(company.getOwnerAccountId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Owner cannot be added as a recruiter");
        }
        if (companyRepository.existsByOwnerAccountId(recruiterAccountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This account is already an owner of another company");
        }
        if (recruiterRepository.existsByAccountId(recruiterAccountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This account is already a recruiter at another company");
        }

        var entity = recruiterRepository.save(mapper.toEntity(company, recruiterAccountId));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<CompanyRecruiterResponse> findByCompany(UUID companyId, Pageable pageable) {
        if (!companyRepository.existsById(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }
        return recruiterRepository.findAllByCompanyId(companyId, pageable).map(mapper::toResponse);
    }

    @Transactional
    public void removeRecruiter(UUID companyId, UUID recruiterAccountId) {
        var currentAccountId = accountContext.requiredAccountId();
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the company owner can remove recruiters");
        }

        var recruiter = recruiterRepository.findByCompanyIdAndAccountId(companyId, recruiterAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter not found in this company"));

        recruiterRepository.deleteById(recruiter.getId());
    }

    @Transactional
    public void removeSelf(UUID companyId) {
        var currentAccountId = accountContext.requiredAccountId();

        if (!companyRepository.existsById(companyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found");
        }

        var recruiter = recruiterRepository.findByCompanyIdAndAccountId(companyId, currentAccountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a recruiter at this company"));

        // TODO: check that this recruiter has no active vacancies in vacancy-app before allowing self-removal (cross-service call)

        recruiterRepository.deleteById(recruiter.getId());
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     */
    @Transactional
    public void deleteByAccountId(UUID accountId) {
        recruiterRepository.deleteByAccountId(accountId);
    }
}
