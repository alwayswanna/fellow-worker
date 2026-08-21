package a.gleb.company_app.adapter.out.persistence;

import a.gleb.company_app.adapter.out.persistence.mapper.CompanyPersistenceMapper;
import a.gleb.company_app.adapter.out.persistence.repository.CompanyEntityRepository;
import a.gleb.company_app.adapter.out.persistence.spec.CompanySpecification;
import a.gleb.company_app.application.port.out.CompanyRepositoryPort;
import a.gleb.company_app.domain.model.Company;
import a.gleb.company_app.domain.model.CompanySearchFilter;
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
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final CompanyEntityRepository companyEntityRepository;
    private final CompanyPersistenceMapper companyPersistenceMapper;

    @Override
    public Company create(Company company) {
        var saved = companyEntityRepository.save(companyPersistenceMapper.toEntity(company));
        return companyPersistenceMapper.toDomain(saved);
    }

    @Override
    public Company update(Company company) {
        var saved = companyEntityRepository.save(companyPersistenceMapper.toEntity(company));
        return companyPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return companyEntityRepository.findById(id).map(companyPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return companyEntityRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        companyEntityRepository.deleteById(id);
    }

    @Override
    public boolean existsByOwnerAccountId(UUID ownerAccountId) {
        return companyEntityRepository.existsByOwnerAccountId(ownerAccountId);
    }

    @Override
    public Optional<Company> findByOwnerAccountId(UUID ownerAccountId) {
        return companyEntityRepository.findByOwnerAccountId(ownerAccountId).map(companyPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Company> search(CompanySearchFilter filter, PageQuery pageQuery) {
        var spec = CompanySpecification.build(filter.name(), filter.industry(), filter.city(), filter.size());
        var page = companyEntityRepository.findAll(spec, toPageable(pageQuery)).map(companyPersistenceMapper::toDomain);
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public void recalculateRating(UUID id) {
        companyEntityRepository.recalculateRating(id);
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
