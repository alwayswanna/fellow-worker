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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumeRepositoryAdapter implements ResumeRepositoryPort {

    private static final int MAX_PAGE_SIZE = 100;

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

    /**
     * Loads the managed entity and mutates it in place (rather than building a new detached entity
     * and merging it) so Hibernate dirty-checks the actual loaded {@code @Version} — a merge of a
     * freshly-built entity would otherwise always carry the Java-default version, turning every
     * optimistic-lock check into a guaranteed (false) conflict after the first real update.
     */
    @Override
    public Resume update(Resume resume) {
        var entity = resumeEntityRepository.findById(resume.getId())
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        resumePersistenceMapper.updateEntity(entity, resume);

        skillEntityRepository.deleteAllByResumeId(entity.getId());
        workExperienceEntityRepository.deleteAllByResumeId(entity.getId());
        educationEntityRepository.deleteAllByResumeId(entity.getId());
        resumeExternalLinkEntityRepository.deleteAllByResumeId(entity.getId());

        var skills = skillEntityRepository.saveAll(resumePersistenceMapper.toSkillEntities(resume.getSkills(), entity));
        var experiences = workExperienceEntityRepository.saveAll(
                resumePersistenceMapper.toWorkExperienceEntities(resume.getExperience(), entity));
        var educations = educationEntityRepository.saveAll(
                resumePersistenceMapper.toEducationEntities(resume.getEducation(), entity));
        var links = resumeExternalLinkEntityRepository.saveAll(
                resumePersistenceMapper.toLinkEntities(resume.getLinks(), entity));

        return resumePersistenceMapper.toDomain(entity, skills, experiences, educations, links);
    }

    /**
     * Transactional on its own (rather than relying on the caller) — {@code ResumeService.uploadPhoto}
     * deliberately isn't {@code @Transactional}, since the MinIO upload it does between the
     * ownership lookup and this write shouldn't hold a DB transaction open.
     */
    @Override
    @Transactional
    public Resume updatePhotoUrl(UUID id, String photoUrl) {
        var entity = resumeEntityRepository.findById(id)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));
        entity.setPhotoUrl(photoUrl);
        var saved = resumeEntityRepository.save(entity);
        return loadFullDomain(saved);
    }

    /**
     * Transactional on its own for the same reason as {@link #updatePhotoUrl} — it's used by
     * {@code ResumeService.uploadPhoto}, which isn't itself wrapped in a transaction.
     */
    @Override
    @Transactional(readOnly = true)
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
        return resumeEntityRepository.findTop100ByAccountId(accountId).stream().map(this::loadFullDomain).toList();
    }

    @Override
    public PageResult<Resume> findAll(PageQuery pageQuery) {
        var page = resumeEntityRepository.findAll(toPageable(pageQuery)).map(this::loadFullDomain);
        return new PageResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    /**
     * Reads the 4 collections off the (lazy, {@code @BatchSize}-backed) entity associations rather
     * than issuing a dedicated {@code findAllByResumeId} per collection — for a page/list of N
     * resumes, Hibernate batches each collection into one "IN (...)" query for the whole page
     * instead of N queries, so this only works called within an active transaction.
     */
    private Resume loadFullDomain(ResumeEntity entity) {
        return resumePersistenceMapper.toDomain(
                entity, entity.getSkills(), entity.getWorkExperiences(), entity.getEducations(), entity.getLinks());
    }

    private Pageable toPageable(PageQuery query) {
        var sort = Sort.unsorted();
        if (query.sortOrders() != null && !query.sortOrders().isEmpty()) {
            var orders = query.sortOrders().stream()
                    .map(o -> o.descending() ? Sort.Order.desc(o.property()) : Sort.Order.asc(o.property()))
                    .toList();
            sort = Sort.by(orders);
        }
        return PageRequest.of(query.page(), Math.min(query.size(), MAX_PAGE_SIZE), sort);
    }
}
