package a.gleb.resume_app.application.service;

import a.gleb.resume_app.application.port.out.CurrentAccountPort;
import a.gleb.resume_app.application.port.out.PhotoStoragePort;
import a.gleb.resume_app.application.port.out.ResumeRepositoryPort;
import a.gleb.resume_app.domain.exception.ResumeNotFoundException;
import a.gleb.resume_app.domain.model.PageQuery;
import a.gleb.resume_app.domain.model.PageResult;
import a.gleb.resume_app.domain.model.Resume;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepositoryPort resumeRepositoryPort;
    private final CurrentAccountPort currentAccountPort;
    private final PhotoStoragePort photoStoragePort;

    @Transactional
    public Resume create(Resume resume) {
        resume.setAccountId(currentAccountPort.requiredAccountId());
        return resumeRepositoryPort.create(resume);
    }

    @Transactional(readOnly = true)
    public Resume findById(UUID id) {
        var accountId = currentAccountPort.requiredAccountId();
        return resumeRepositoryPort.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<Resume> findAll(PageQuery pageQuery) {
        return resumeRepositoryPort.findAll(pageQuery);
    }

    @Transactional(readOnly = true)
    public List<Resume> findMy() {
        var accountId = currentAccountPort.requiredAccountId();
        return resumeRepositoryPort.findAllByAccountId(accountId);
    }

    @Transactional
    public Resume update(UUID id, Resume incoming) {
        var accountId = currentAccountPort.requiredAccountId();
        var existing = resumeRepositoryPort.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        incoming.setId(existing.getId());
        incoming.setAccountId(existing.getAccountId());
        incoming.setCreatedAt(existing.getCreatedAt());
        incoming.setCreatedBy(existing.getCreatedBy());
        incoming.setPhotoUrl(existing.getPhotoUrl());

        return resumeRepositoryPort.update(incoming);
    }

    @Transactional
    public Resume uploadPhoto(UUID resumeId, MultipartFile file) {
        var accountId = currentAccountPort.requiredAccountId();
        var resume = resumeRepositoryPort.findByIdAndAccountId(resumeId, accountId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        if (resume.getPhotoUrl() != null) {
            photoStoragePort.delete(resume.getPhotoUrl());
        }

        String photoUrl = photoStoragePort.upload(resumeId, file);
        return resumeRepositoryPort.updatePhotoUrl(resumeId, photoUrl);
    }

    @Transactional
    public void delete(UUID id) {
        var accountId = currentAccountPort.requiredAccountId();
        if (!resumeRepositoryPort.existsByIdAndAccountId(id, accountId)) {
            throw new ResumeNotFoundException("Resume not found");
        }
        resumeRepositoryPort.deleteByIdAndAccountId(id, accountId);
    }

    /**
     * Called when user-app reports the owning account was deleted (`USER_DELETED` event).
     * DB-level `ON DELETE CASCADE` takes care of work experience/education/skill/link rows.
     */
    @Transactional
    public void deleteAllForAccount(UUID accountId) {
        resumeRepositoryPort.deleteAllByAccountId(accountId);
        log.info("ResumeService: deleted resumes for removed account [userId={}]", accountId);
    }
}
