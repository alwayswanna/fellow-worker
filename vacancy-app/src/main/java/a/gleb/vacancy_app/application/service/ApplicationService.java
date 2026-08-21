package a.gleb.vacancy_app.application.service;

import a.gleb.vacancy_app.application.port.out.ApplicationRepositoryPort;
import a.gleb.vacancy_app.application.port.out.CurrentAccountPort;
import a.gleb.vacancy_app.application.port.out.VacancyRepositoryPort;
import a.gleb.vacancy_app.domain.exception.AlreadyAppliedException;
import a.gleb.vacancy_app.domain.exception.ApplicationAlreadyWithdrawnException;
import a.gleb.vacancy_app.domain.exception.ApplicationNotFoundException;
import a.gleb.vacancy_app.domain.exception.InvalidApplicationStatusException;
import a.gleb.vacancy_app.domain.exception.VacancyNotFoundException;
import a.gleb.vacancy_app.domain.model.Application;
import a.gleb.vacancy_app.domain.model.PageQuery;
import a.gleb.vacancy_app.domain.model.PageResult;
import a.gleb.vacancy_app.domain.model.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepositoryPort applicationRepositoryPort;
    private final VacancyRepositoryPort vacancyRepositoryPort;
    private final CurrentAccountPort currentAccountPort;

    @Transactional
    public Application apply(UUID vacancyId) {
        var applicantId = currentAccountPort.requiredAccountId();

        if (!vacancyRepositoryPort.existsById(vacancyId)) {
            throw new VacancyNotFoundException("Vacancy not found");
        }

        var existing = applicationRepositoryPort.findByVacancyIdAndApplicantAccountId(vacancyId, applicantId);
        if (existing.isPresent()) {
            var app = existing.get();
            if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
                throw new ApplicationAlreadyWithdrawnException(
                        "You have already withdrawn your application for this vacancy and cannot re-apply");
            }
            throw new AlreadyAppliedException("You have already applied to this vacancy");
        }

        var application = Application.builder()
                .vacancyId(vacancyId)
                .applicantAccountId(applicantId)
                .status(ApplicationStatus.PENDING)
                .build();
        return applicationRepositoryPort.save(application);
    }

    @Transactional
    public void withdraw(UUID vacancyId) {
        var applicantId = currentAccountPort.requiredAccountId();
        var app = applicationRepositoryPort
                .findByVacancyIdAndApplicantAccountId(vacancyId, applicantId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new ApplicationAlreadyWithdrawnException("Application already withdrawn");
        }

        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepositoryPort.save(app);
    }

    @Transactional(readOnly = true)
    public Optional<Application> getMyApplication(UUID vacancyId) {
        var applicantId = currentAccountPort.requiredAccountId();
        return applicationRepositoryPort.findByVacancyIdAndApplicantAccountId(vacancyId, applicantId);
    }

    @Transactional(readOnly = true)
    public List<Application> getMyApplications() {
        var applicantId = currentAccountPort.requiredAccountId();
        return applicationRepositoryPort.findAllByApplicantAccountId(applicantId);
    }

    @Transactional(readOnly = true)
    public PageResult<Application> getVacancyApplications(UUID vacancyId, PageQuery pageQuery) {
        if (!vacancyRepositoryPort.existsById(vacancyId)) {
            throw new VacancyNotFoundException("Vacancy not found");
        }
        return applicationRepositoryPort.findAllByVacancyId(vacancyId, pageQuery);
    }

    @Transactional
    public Application updateStatus(UUID vacancyId, UUID applicationId, ApplicationStatus newStatus) {
        if (newStatus == ApplicationStatus.PENDING || newStatus == ApplicationStatus.WITHDRAWN) {
            throw new InvalidApplicationStatusException(
                    "Recruiter can only set status to REVIEWED, ACCEPTED, or REJECTED");
        }

        var app = applicationRepositoryPort.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (!app.getVacancyId().equals(vacancyId)) {
            throw new ApplicationNotFoundException("Application not found for this vacancy");
        }

        app.setStatus(newStatus);
        return applicationRepositoryPort.save(app);
    }

    /**
     * Called when user-app reports the applicant account was deleted (`USER_DELETED` event).
     */
    @Transactional
    public void deleteAllForAccount(UUID accountId) {
        applicationRepositoryPort.deleteAllByApplicantAccountId(accountId);
        log.info("ApplicationService: deleted applications for removed account [userId={}]", accountId);
    }
}
