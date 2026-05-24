package a.gleb.vacancy_app.service;

import a.gleb.vacancy_app.db.entity.ApplicationEntity;
import a.gleb.vacancy_app.db.repository.ApplicationRepository;
import a.gleb.vacancy_app.db.repository.VacancyRepository;
import a.gleb.vacancy_app.model.enums.ApplicationStatus;
import a.gleb.vacancy_app.model.response.ApplicationResponse;
import a.gleb.vacancy_app.security.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final VacancyRepository vacancyRepository;
    private final AccountContext accountContext;

    @Transactional
    public ApplicationResponse apply(UUID vacancyId) {
        var applicantId = accountContext.requiredAccountId();

        if (!vacancyRepository.existsById(vacancyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacancy not found");
        }

        var existing = applicationRepository.findByVacancyIdAndApplicantAccountId(vacancyId, applicantId);
        if (existing.isPresent()) {
            var app = existing.get();
            if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "You have already withdrawn your application for this vacancy and cannot re-apply");
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied to this vacancy");
        }

        var entity = ApplicationEntity.builder()
                .vacancyId(vacancyId)
                .applicantAccountId(applicantId)
                .status(ApplicationStatus.PENDING)
                .build();
        return toResponse(applicationRepository.save(entity));
    }

    @Transactional
    public void withdraw(UUID vacancyId) {
        var applicantId = accountContext.requiredAccountId();
        var app = applicationRepository
                .findByVacancyIdAndApplicantAccountId(vacancyId, applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Application already withdrawn");
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public Optional<ApplicationResponse> getMyApplication(UUID vacancyId) {
        var applicantId = accountContext.requiredAccountId();
        return applicationRepository
                .findByVacancyIdAndApplicantAccountId(vacancyId, applicantId)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications() {
        var applicantId = accountContext.requiredAccountId();
        return applicationRepository.findAllByApplicantAccountId(applicantId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getVacancyApplications(UUID vacancyId, Pageable pageable) {
        if (!vacancyRepository.existsById(vacancyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacancy not found");
        }
        return applicationRepository.findAllByVacancyId(vacancyId, pageable).map(this::toResponse);
    }

    @Transactional
    public ApplicationResponse updateStatus(UUID vacancyId, UUID applicationId, ApplicationStatus newStatus) {
        if (newStatus == ApplicationStatus.PENDING || newStatus == ApplicationStatus.WITHDRAWN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Recruiter can only set status to REVIEWED, ACCEPTED, or REJECTED");
        }

        var app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (!app.getVacancyId().equals(vacancyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for this vacancy");
        }

        app.setStatus(newStatus);
        return toResponse(applicationRepository.save(app));
    }

    private ApplicationResponse toResponse(ApplicationEntity e) {
        return ApplicationResponse.builder()
                .id(e.getId())
                .vacancyId(e.getVacancyId())
                .applicantAccountId(e.getApplicantAccountId())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(e.getUpdatedAt() != null ? e.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
