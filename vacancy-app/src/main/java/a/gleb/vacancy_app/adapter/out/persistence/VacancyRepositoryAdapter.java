package a.gleb.vacancy_app.adapter.out.persistence;

import a.gleb.vacancy_app.adapter.out.persistence.mapper.VacancyPersistenceMapper;
import a.gleb.vacancy_app.adapter.out.persistence.repository.VacancyEntityRepository;
import a.gleb.vacancy_app.adapter.out.persistence.repository.VacancySkillEntityRepository;
import a.gleb.vacancy_app.adapter.out.persistence.spec.VacancySpecification;
import a.gleb.vacancy_app.application.port.out.VacancyRepositoryPort;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.Vacancy;
import a.gleb.vacancy_app.domain.model.VacancySearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VacancyRepositoryAdapter implements VacancyRepositoryPort {

    private final VacancyEntityRepository vacancyEntityRepository;
    private final VacancySkillEntityRepository vacancySkillEntityRepository;
    private final VacancyPersistenceMapper vacancyPersistenceMapper;

    @Override
    public Vacancy create(Vacancy vacancy) {
        var saved = vacancyEntityRepository.save(vacancyPersistenceMapper.toEntity(vacancy));
        var skills = vacancySkillEntityRepository.saveAll(
                vacancyPersistenceMapper.toSkillEntities(vacancy.getSkills(), saved));
        return vacancyPersistenceMapper.toDomain(saved, skills);
    }

    @Override
    public Vacancy update(Vacancy vacancy) {
        vacancySkillEntityRepository.deleteAllByVacancyId(vacancy.getId());
        var updated = vacancyEntityRepository.save(vacancyPersistenceMapper.toEntity(vacancy));
        var skills = vacancySkillEntityRepository.saveAll(
                vacancyPersistenceMapper.toSkillEntities(vacancy.getSkills(), updated));
        return vacancyPersistenceMapper.toDomain(updated, skills);
    }

    @Override
    public Optional<Vacancy> findById(UUID id) {
        return vacancyEntityRepository.findById(id).map(vacancyPersistenceMapper::toDomain);
    }

    @Override
    public List<Vacancy> findAllById(List<UUID> ids) {
        return vacancyEntityRepository.findAllById(ids).stream().map(vacancyPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return vacancyEntityRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        vacancyEntityRepository.deleteById(id);
    }

    /**
     * Bulk delete — DB-level `ON DELETE CASCADE` on vacancy_skill/application takes care of
     * dependent rows.
     */
    @Override
    public void deleteAllByCompanyId(UUID companyId) {
        vacancyEntityRepository.deleteAllByCompanyId(companyId);
    }

    @Override
    public PageResult<Vacancy> search(VacancySearchFilter filter, PageQuery pageQuery) {
        var spec = VacancySpecification.build(
                filter.title(), filter.companyId(), filter.city(), filter.employmentType(),
                filter.workFormat(), filter.experienceLevel(), filter.salaryFrom(), filter.salaryTo(),
                filter.status(), filter.skills()
        );
        var page = vacancyEntityRepository.findAll(spec, PersistencePageSupport.toPageable(pageQuery));
        return PersistencePageSupport.toPageResult(page, vacancyPersistenceMapper::toDomain);
    }
}
