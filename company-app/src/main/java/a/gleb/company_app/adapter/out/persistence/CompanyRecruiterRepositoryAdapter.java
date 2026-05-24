package a.gleb.company_app.adapter.out.persistence;

import a.gleb.company_app.adapter.out.persistence.mapper.CompanyRecruiterPersistenceMapper;
import a.gleb.company_app.adapter.out.persistence.repository.CompanyEntityRepository;
import a.gleb.company_app.adapter.out.persistence.repository.CompanyRecruiterEntityRepository;
import a.gleb.company_app.application.port.out.CompanyRecruiterRepositoryPort;
import a.gleb.company_app.domain.model.CompanyRecruiter;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyRecruiterRepositoryAdapter implements CompanyRecruiterRepositoryPort {

    private final CompanyRecruiterEntityRepository companyRecruiterEntityRepository;
    private final CompanyEntityRepository companyEntityRepository;
    private final CompanyRecruiterPersistenceMapper companyRecruiterPersistenceMapper;

    @Override
    public CompanyRecruiter save(CompanyRecruiter recruiter) {
        var companyRef = companyEntityRepository.getReferenceById(recruiter.getCompanyId());
        var saved = companyRecruiterEntityRepository.save(companyRecruiterPersistenceMapper.toEntity(recruiter, companyRef));
        return companyRecruiterPersistenceMapper.toDomain(saved);
    }

    @Override
    public PageResult<CompanyRecruiter> findAllByCompanyId(UUID companyId, PageQuery pageQuery) {
        var page = companyRecruiterEntityRepository.findAllByCompanyId(companyId, toPageable(pageQuery))
                .map(companyRecruiterPersistenceMapper::toDomain);
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public Optional<CompanyRecruiter> findByCompanyIdAndAccountId(UUID companyId, UUID accountId) {
        return companyRecruiterEntityRepository.findByCompanyIdAndAccountId(companyId, accountId)
                .map(companyRecruiterPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCompanyId(UUID companyId) {
        return companyRecruiterEntityRepository.existsByCompanyId(companyId);
    }

    @Override
    public boolean existsByAccountId(UUID accountId) {
        return companyRecruiterEntityRepository.existsByAccountId(accountId);
    }

    @Override
    public Optional<CompanyRecruiter> findByAccountId(UUID accountId) {
        return companyRecruiterEntityRepository.findByAccountId(accountId).map(companyRecruiterPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        companyRecruiterEntityRepository.deleteById(id);
    }

    @Override
    public void deleteByAccountId(UUID accountId) {
        companyRecruiterEntityRepository.deleteByAccountId(accountId);
    }

    private Pageable toPageable(PageQuery query) {
        var sort = Sort.unsorted();
        if (query.sortOrders() != null && !query.sortOrders().isEmpty()) {
            var orders = query.sortOrders().stream()
                    .map(o -> o.descending() ? Sort.Order.desc(o.property()) : Sort.Order.asc(o.property()))
                    .toList();
            sort = Sort.by(orders);
        }
        return PageRequest.of(query.page(), query.size(), sort);
    }
}
