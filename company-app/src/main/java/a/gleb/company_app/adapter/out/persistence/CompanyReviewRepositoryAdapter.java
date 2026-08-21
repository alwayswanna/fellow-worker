package a.gleb.company_app.adapter.out.persistence;

import a.gleb.company_app.adapter.out.persistence.entity.CompanyReviewEntity;
import a.gleb.company_app.adapter.out.persistence.mapper.CompanyReviewPersistenceMapper;
import a.gleb.company_app.adapter.out.persistence.repository.CompanyEntityRepository;
import a.gleb.company_app.adapter.out.persistence.repository.CompanyReviewEntityRepository;
import a.gleb.company_app.application.port.out.CompanyReviewRepositoryPort;
import a.gleb.company_app.domain.model.CompanyReview;
import a.gleb.company_app.domain.model.PageQuery;
import a.gleb.company_app.domain.model.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyReviewRepositoryAdapter implements CompanyReviewRepositoryPort {

    private final CompanyReviewEntityRepository companyReviewEntityRepository;
    private final CompanyEntityRepository companyEntityRepository;
    private final CompanyReviewPersistenceMapper companyReviewPersistenceMapper;

    @Override
    public CompanyReview save(CompanyReview review) {
        var companyRef = companyEntityRepository.getReferenceById(review.getCompanyId());
        var saved = companyReviewEntityRepository.save(companyReviewPersistenceMapper.toEntity(review, companyRef));
        return companyReviewPersistenceMapper.toDomain(saved);
    }

    @Override
    public PageResult<CompanyReview> findAllByCompanyId(UUID companyId, PageQuery pageQuery) {
        var page = companyReviewEntityRepository.findAllByCompanyId(companyId, toPageable(pageQuery))
                .map(companyReviewPersistenceMapper::toDomain);
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public Optional<CompanyReview> findByIdAndAccountId(UUID id, UUID accountId) {
        return companyReviewEntityRepository.findByIdAndAccountId(id, accountId).map(companyReviewPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCompanyIdAndAccountId(UUID companyId, UUID accountId) {
        return companyReviewEntityRepository.existsByCompanyIdAndAccountId(companyId, accountId);
    }

    @Override
    public List<CompanyReview> findAllByAccountId(UUID accountId) {
        return companyReviewEntityRepository.findAllByAccountId(accountId).stream()
                .map(companyReviewPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        companyReviewEntityRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<CompanyReview> reviews) {
        var ids = reviews.stream().map(CompanyReview::getId).toList();
        var entities = companyReviewEntityRepository.findAllById(ids);
        companyReviewEntityRepository.deleteAll(entities);
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
