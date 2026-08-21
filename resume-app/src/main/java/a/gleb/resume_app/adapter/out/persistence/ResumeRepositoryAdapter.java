package a.gleb.resume_app.adapter.out.persistence;

import a.gleb.resume_app.adapter.out.persistence.entity.ResumeEntity;
import a.gleb.resume_app.adapter.out.persistence.mapper.ResumePersistenceMapper;
import a.gleb.resume_app.adapter.out.persistence.repository.EducationEntityRepository;
import a.gleb.resume_app.adapter.out.persistence.repository.ResumeEntityRepository;
import a.gleb.resume_app.adapter.out.persistence.repository.ResumeExternalLinkEntityRepository;
import a.gleb.resume_app.adapter.out.persistence.repository.SkillEntityRepository;
import a.gleb.resume_app.adapter.out.persistence.repository.WorkExperienceEntityRepository;
import a.gleb.resume_app.application.port.out.ResumeRepositoryPort;
import a.gleb.resume_app.domain.exception.ResumeNotFoundException;
import a.gleb.resume_app.domain.model.PageQuery;
import a.gleb.resume_app.domain.model.PageResult;
import a.gleb.resume_app.domain.model.Resume;
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
public class ResumeRepositoryAdapter implements ResumeRepositoryPort {

    private final ResumeEntityRepository resumeEntityRepository;
    private final SkillEntityRepository skillEntityRepository;
    private final WorkExperienceEntityRepository workExperienceEntityRepository;
    private final EducationEntityRepository educationEntityRepository;
    private final ResumeExternalLinkEntityRepository resumeExternalLinkEntityRepository;
    private final ResumePersistenceMapper resumePersistenceMapper;

    @Override
    public Resume create(Resume resume) {
        var saved = resumeEntityRepository.save(resumePersistenceMapper.toEntity(resume));

        var skills = skillEntityRepository.saveAll(resumePersistenceMapper.toSkillEntities(resume.getSkills(), saved));
        var experiences = workExperienceEntityRepository.saveAll(
                resumePersistenceMapper.toWorkExperienceEntities(resume.getExperience(), saved));
        var educations = educationEntityRepository.saveAll(
                resumePersistenceMapper.toEducationEntities(resume.getEducation(), saved));
        var links = resumeExternalLinkEntityRepository.saveAll(
                resumePersistenceMapper.toLinkEntities(resume.getLinks(), saved));

        return resumePersistenceMapper.toDomain(saved, skills, experiences, educations, links);
    }

    @Override
    public Resume update(Resume resume) {
        skillEntityRepository.deleteAllByResumeId(resume.getId());
        workExperienceEntityRepository.deleteAllByResumeId(resume.getId());
        educationEntityRepository.deleteAllByResumeId(resume.getId());
        resumeExternalLinkEntityRepository.deleteAllByResumeId(resume.getId());

        var updated = resumeEntityRepository.save(resumePersistenceMapper.toEntity(resume));

        var skills = skillEntityRepository.saveAll(resumePersistenceMapper.toSkillEntities(resume.getSkills(), updated));
        var experiences = workExperienceEntityRepository.saveAll(
                resumePersistenceMapper.toWorkExperienceEntities(resume.getExperience(), updated));
        var educations = educationEntityRepository.saveAll(
                resumePersistenceMapper.toEducationEntities(resume.getEducation(), updated));
        var links = resumeExternalLinkEntityRepository.saveAll(
                resumePersistenceMapper.toLinkEntities(resume.getLinks(), updated));

        return resumePersistenceMapper.toDomain(updated, skills, experiences, educations, links);
    }

    @Override
    public Resume updatePhotoUrl(UUID id, String photoUrl) {
        var entity = resumeEntityRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));
        entity.setPhotoUrl(photoUrl);
        var saved = resumeEntityRepository.save(entity);
        return loadFullDomain(saved);
    }

    @Override
    public Optional<Resume> findByIdAndAccountId(UUID id, UUID accountId) {
        return resumeEntityRepository.findByIdAndAccountId(id, accountId).map(this::loadFullDomain);
    }

    @Override
    public boolean existsByIdAndAccountId(UUID id, UUID accountId) {
        return resumeEntityRepository.existsByIdAndAccountId(id, accountId);
    }

    @Override
    public void deleteByIdAndAccountId(UUID id, UUID accountId) {
        resumeEntityRepository.deleteByIdAndAccountId(id, accountId);
    }

    @Override
    public void deleteAllByAccountId(UUID accountId) {
        resumeEntityRepository.deleteAllByAccountId(accountId);
    }

    @Override
    public List<Resume> findAllByAccountId(UUID accountId) {
        return resumeEntityRepository.findAllByAccountId(accountId).stream().map(this::loadFullDomain).toList();
    }

    @Override
    public PageResult<Resume> findAll(PageQuery pageQuery) {
        var page = resumeEntityRepository.findAll(toPageable(pageQuery)).map(this::loadFullDomain);
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private Resume loadFullDomain(ResumeEntity entity) {
        var skills = skillEntityRepository.findAllByResumeId(entity.getId());
        var experiences = workExperienceEntityRepository.findAllByResumeId(entity.getId());
        var educations = educationEntityRepository.findAllByResumeId(entity.getId());
        var links = resumeExternalLinkEntityRepository.findAllByResumeId(entity.getId());
        return resumePersistenceMapper.toDomain(entity, skills, experiences, educations, links);
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
