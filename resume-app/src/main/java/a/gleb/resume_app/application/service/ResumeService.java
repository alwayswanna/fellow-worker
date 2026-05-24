package a.gleb.resume_app.application.service;

import a.gleb.resume_app.application.port.out.CurrentAccountPort;
import a.gleb.resume_app.application.port.out.PhotoStoragePort;
import a.gleb.resume_app.application.port.out.ResumeRepositoryPort;
import a.gleb.resume_app.constant.ResumeAppConstant;
import a.gleb.resume_app.domain.exception.InvalidPhotoException;
import a.gleb.resume_app.domain.exception.ResumeNotFoundException;
import a.gleb.resume_app.domain.model.PageQuery;
import a.gleb.resume_app.domain.model.PageResult;
import a.gleb.resume_app.domain.model.Resume;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    /** Roles allowed to look up another account's resumes (e.g. a recruiter
     * reviewing an applicant found via user search). */
    private static final Set<String> RESUME_LOOKUP_ROLES = Set.of("RECRUITER", "COMPANY_FOUNDER");

    private final ResumeRepositoryPort resumeRepositoryPort;
    private final CurrentAccountPort currentAccountPort;
    private final PhotoStoragePort photoStoragePort;
    private final MeterRegistry meterRegistry;

    @Transactional
    public Resume create(Resume resume) {
        resume.setAccountId(currentAccountPort.requiredAccountId());
        var created = resumeRepositoryPort.create(resume);
        meterRegistry.counter("resume.created").increment();
        return created;
    }

    @Transactional(readOnly = true)
    public Resume findById(UUID id) {
        var accountId = currentAccountPort.requiredAccountId();
        return resumeRepositoryPort.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));
    }

    /**
     * Browses every resume in the system (used by the admin resume search) — unlike
     * {@link #findAllByAccountId}, which looks up one specific account, this is a bulk listing
     * with full PII (email, phone, birth date, work history), so it's restricted to admins.
     *
     * @throws ResponseStatusException 403 if the caller isn't an admin.
     */
    @Transactional(readOnly = true)
    public PageResult<Resume> findAll(PageQuery pageQuery) {
        if (!ResumeAppConstant.ADMIN_ROLE_CODE.equals(currentAccountPort.requiredRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can browse all resumes");
        }
        return resumeRepositoryPort.findAll(pageQuery);
    }

    @Transactional(readOnly = true)
    public List<Resume> findMy() {
        var accountId = currentAccountPort.requiredAccountId();
        return resumeRepositoryPort.findAllByAccountId(accountId);
    }

    /**
     * Service-to-service check (e.g. vacancy-app gating whether an applicant may apply) — takes
     * the target {@code accountId} explicitly rather than the caller's own id, so it's not scoped
     * by {@link CurrentAccountPort} like the other methods here.
     */
    @Transactional(readOnly = true)
    public boolean existsForAccount(UUID accountId) {
        return resumeRepositoryPort.existsByAccountId(accountId);
    }

    /**
     * Looks up another account's resumes by id — e.g. a recruiter reviewing
     * an applicant found via user search.
     *
     * @throws ResponseStatusException 403 if the caller isn't a recruiter or company founder.
     */
    @Transactional(readOnly = true)
    public List<Resume> findAllByAccountId(UUID accountId) {
        if (!RESUME_LOOKUP_ROLES.contains(currentAccountPort.requiredRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only recruiters or company founders can look up another account's resumes");
        }
        return resumeRepositoryPort.findAllByAccountId(accountId);
    }

    /**
     * Ownership is checked here, not relied upon in the adapter: {@link ResumeRepositoryPort#update}
     * loads and mutates the entity by id alone (see {@code ResumeRepositoryAdapter.update} for why),
     * so without this check a caller could otherwise update another account's resume by id.
     * accountId/photoUrl/audit fields aren't touched by the update — they're preserved automatically
     * since the adapter mutates the already-loaded managed entity rather than rebuilding it.
     */
    @Transactional
    public Resume update(UUID id, Resume incoming) {
        var accountId = currentAccountPort.requiredAccountId();
        if (!resumeRepositoryPort.existsByIdAndAccountId(id, accountId)) {
            throw new ResumeNotFoundException("Resume not found");
        }

        incoming.setId(id);
        return resumeRepositoryPort.update(incoming);
    }

    /**
     * Not wrapped in {@code @Transactional}: the actual file transfer to MinIO happens here, and
     * it can take seconds (including retry backoff) — holding a DB transaction/connection open for
     * that whole span would needlessly tie up the pool. The two DB round trips this makes
     * ({@link ResumeRepositoryPort#findByIdAndAccountId} and {@link ResumeRepositoryPort#updatePhotoUrl})
     * are each transactional on their own at the adapter level.
     * <p>
     * Uploads the new photo <em>before</em> touching the old one, and only deletes the old object
     * once both the upload and the DB pointer update succeeded — and only if its key actually
     * differs from the new one (same extension re-upload overwrites in place via the same object
     * key). Deleting first would leave the resume pointing at a gone object the moment the new
     * upload fails for any reason.
     */
    public Resume uploadPhoto(UUID resumeId, MultipartFile file) {
        validatePhoto(file);
        var accountId = currentAccountPort.requiredAccountId();
        var resume = resumeRepositoryPort.findByIdAndAccountId(resumeId, accountId)
                .orElseThrow(() -> new ResumeNotFoundException("Resume not found"));

        String newPhotoUrl = photoStoragePort.upload(resumeId, file);
        var updated = resumeRepositoryPort.updatePhotoUrl(resumeId, newPhotoUrl);

        String oldPhotoUrl = resume.getPhotoUrl();
        if (oldPhotoUrl != null && !oldPhotoUrl.equals(newPhotoUrl)) {
            // Best-effort: the new photo is already uploaded and recorded above, so a leftover
            // old object is a minor storage cleanup to retry later, not a reason to fail this call.
            try {
                photoStoragePort.delete(oldPhotoUrl);
            } catch (Exception e) {
                log.warn("Failed to delete superseded photo, will remain orphaned [photoUrl={}]", oldPhotoUrl, e);
            }
        }

        return updated;
    }

    private void validatePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPhotoException("Photo file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ResumeAppConstant.ALLOWED_PHOTO_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new InvalidPhotoException(
                    "Unsupported content type '%s', expected one of %s"
                            .formatted(contentType, ResumeAppConstant.ALLOWED_PHOTO_CONTENT_TYPES));
        }

        String extension = StringUtils.substringAfterLast(
                StringUtils.defaultString(file.getOriginalFilename()), ".").toLowerCase(Locale.ROOT);
        if (extension.isEmpty() || !ResumeAppConstant.ALLOWED_PHOTO_EXTENSIONS.contains(extension)) {
            throw new InvalidPhotoException(
                    "Unsupported file extension, expected one of %s".formatted(ResumeAppConstant.ALLOWED_PHOTO_EXTENSIONS));
        }
    }

    @Transactional
    public void delete(UUID id) {
        var accountId = currentAccountPort.requiredAccountId();
        if (!resumeRepositoryPort.existsByIdAndAccountId(id, accountId)) {
            throw new ResumeNotFoundException("Resume not found");
        }
        resumeRepositoryPort.deleteByIdAndAccountId(id, accountId);
        meterRegistry.counter("resume.deleted").increment();
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
