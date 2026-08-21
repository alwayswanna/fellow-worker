package a.gleb.company_app.service;

import a.gleb.company_app.db.repository.CompanyRecruiterRepository;
import a.gleb.company_app.db.repository.CompanyRepository;
import a.gleb.company_app.db.spec.CompanySpecification;
import a.gleb.company_app.mapper.CompanyMapper;
import a.gleb.company_app.model.enums.CompanySize;
import a.gleb.company_app.model.request.CompanyRequest;
import a.gleb.company_app.model.response.CompanyResponse;
import a.gleb.company_app.security.AccountContext;
import a.gleb.fellow_worker.kafka.event.CompanyEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyRecruiterRepository recruiterRepository;
    private final CompanyMapper mapper;
    private final AccountContext accountContext;
    private final OutboxEventService outboxEventService;

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        var currentAccountId = accountContext.requiredAccountId();

        if (companyRepository.existsByOwnerAccountId(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already own a company");
        }
        if (recruiterRepository.existsByAccountId(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already a recruiter at another company");
        }

        var entity = companyRepository.save(mapper.toEntity(currentAccountId, request));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        return companyRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
    }

    @Transactional(readOnly = true)
    public Optional<CompanyResponse> findMyCompany() {
        var currentAccountId = accountContext.requiredAccountId();
        // Owner
        var asOwner = companyRepository.findByOwnerAccountId(currentAccountId);
        if (asOwner.isPresent()) return asOwner.map(mapper::toResponse);
        // Recruiter
        return recruiterRepository.findByAccountId(currentAccountId)
                .map(r -> mapper.toResponse(r.getCompany()));
    }

    @Transactional(readOnly = true)
    public Page<CompanyResponse> findAll(String name, String industry, String city, CompanySize size, Pageable pageable) {
        var spec = CompanySpecification.build(name, industry, city, size);
        return companyRepository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        var currentAccountId = accountContext.requiredAccountId();
        var existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        if (!existing.getOwnerAccountId().equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the company owner can update it");
        }

        var updated = companyRepository.save(mapper.toEntity(existing, request));
        return mapper.toResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        var currentAccountId = accountContext.requiredAccountId();
        var company = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        if (!company.getOwnerAccountId().equals(currentAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the company owner can delete it");
        }
        if (recruiterRepository.existsByCompanyId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete company with active recruiters. Remove all recruiters first");
        }

        // TODO: check that company has no active vacancies in vacancy-app (cross-service call)

        companyRepository.deleteById(id);
    }

    /**
     * Called when user-app reports the account was deleted (`USER_DELETED` event).
     * If the account owns a company, it's deleted (DB `ON DELETE CASCADE` removes its remaining
     * recruiters/reviews) and a `COMPANY_DELETED` outbox event is emitted so vacancy-app can drop
     * the company's vacancies/applications in turn.
     */
    @Transactional
    public void deleteOwnedByAccount(UUID accountId) {
        companyRepository.findByOwnerAccountId(accountId).ifPresent(company -> {
            outboxEventService.saveEvent(company, CompanyEventType.COMPANY_DELETED);
            companyRepository.delete(company);
            log.info("CompanyService: deleted company owned by removed account [companyId={}, userId={}]",
                    company.getId(), accountId);
        });
    }
}
