package a.gleb.resume_app.service;

import a.gleb.resume_app.db.entity.*;
import a.gleb.resume_app.db.repository.*;
import a.gleb.resume_app.mapper.ResumeMapper;
import a.gleb.resume_app.model.request.ResumeRequest;
import a.gleb.resume_app.model.response.ResumeResponse;
import a.gleb.resume_app.security.AccountContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final EducationRepository educationRepository;
    private final ResumeExternalLinkRepository linkRepository;
    private final ResumeMapper mapper;
    private final AccountContext accountContext;
    private final MinioService minioService;

    @Transactional
    public ResumeResponse create(ResumeRequest request) {
        var accountId = accountContext.getAccountId();
        var resume = resumeRepository.save(mapper.toResumeEntity(accountId, request));

        var skills = skillRepository.saveAll(mapper.toSkillEntities(request.skills(), resume));
        var experiences = workExperienceRepository.saveAll(mapper.toWorkExperienceEntities(request.experience(), resume));
        var educations = educationRepository.saveAll(mapper.toEducationEntities(request.education(), resume));
        var links = linkRepository.saveAll(mapper.toLinkEntities(request.links(), resume));

        return mapper.toResponse(resume, skills, experiences, educations, links);
    }

    @Transactional(readOnly = true)
    public ResumeResponse findById(UUID id) {
        var accountId = accountContext.getAccountId();
        var resume = resumeRepository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));
        return mapper.toResponse(resume);
    }

    @Transactional(readOnly = true)
    public Page<ResumeResponse> findAll(Pageable pageable) {
        return resumeRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> findMy() {
        var accountId = accountContext.getAccountId();
        return resumeRepository.findAllByAccountId(accountId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ResumeResponse update(UUID id, ResumeRequest request) {
        var accountId = accountContext.getAccountId();
        var existing = resumeRepository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        skillRepository.deleteAllByResumeId(id);
        workExperienceRepository.deleteAllByResumeId(id);
        educationRepository.deleteAllByResumeId(id);
        linkRepository.deleteAllByResumeId(id);

        var updated = resumeRepository.save(mapper.toResumeEntity(existing, request));

        var skills = skillRepository.saveAll(mapper.toSkillEntities(request.skills(), updated));
        var experiences = workExperienceRepository.saveAll(mapper.toWorkExperienceEntities(request.experience(), updated));
        var educations = educationRepository.saveAll(mapper.toEducationEntities(request.education(), updated));
        var links = linkRepository.saveAll(mapper.toLinkEntities(request.links(), updated));

        return mapper.toResponse(updated, skills, experiences, educations, links);
    }

    @Transactional
    public ResumeResponse uploadPhoto(UUID resumeId, MultipartFile file) {
        var accountId = accountContext.getAccountId();
        var resume = resumeRepository.findByIdAndAccountId(resumeId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));

        if (resume.getPhotoUrl() != null) {
            minioService.deletePhoto(resume.getPhotoUrl());
        }

        String photoUrl = minioService.uploadPhoto(resumeId, file);
        resume.setPhotoUrl(photoUrl);
        resumeRepository.save(resume);

        return mapper.toResponse(resume);
    }

    @Transactional
    public void delete(UUID id) {
        var accountId = accountContext.getAccountId();
        if (!resumeRepository.existsByIdAndAccountId(id, accountId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found");
        }
        resumeRepository.deleteByIdAndAccountId(id, accountId);
    }

    /**
     * Called when user-app reports the owning account was deleted (`USER_DELETED` event).
     * DB-level `ON DELETE CASCADE` takes care of work experience/education/skill/link rows.
     */
    @Transactional
    public void deleteAllForAccount(UUID accountId) {
        resumeRepository.deleteAllByAccountId(accountId);
        log.info("ResumeService: deleted resumes for removed account [userId={}]", accountId);
    }
}
