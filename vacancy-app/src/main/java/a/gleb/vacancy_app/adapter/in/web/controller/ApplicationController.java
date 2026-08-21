package a.gleb.vacancy_app.adapter.in.web.controller;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.vacancy_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.vacancy_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.vacancy_app.adapter.in.web.dto.ApplicationResponse;
import a.gleb.vacancy_app.adapter.in.web.dto.UpdateApplicationStatusRequest;
import a.gleb.vacancy_app.adapter.in.web.mapper.ApplicationWebMapper;
import a.gleb.vacancy_app.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationWebMapper applicationWebMapper;

    /** APPLICANT: apply to a vacancy */
    @PostMapping("/api/v1/vacancies/{vacancyId}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(@PathVariable UUID vacancyId) {
        return applicationWebMapper.toResponse(applicationService.apply(vacancyId));
    }

    /** APPLICANT: withdraw application */
    @DeleteMapping("/api/v1/vacancies/{vacancyId}/applications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable UUID vacancyId) {
        applicationService.withdraw(vacancyId);
    }

    /** APPLICANT: check own application status for a vacancy */
    @GetMapping("/api/v1/vacancies/{vacancyId}/applications/my")
    public Optional<ApplicationResponse> getMyApplication(@PathVariable UUID vacancyId) {
        return applicationService.getMyApplication(vacancyId).map(applicationWebMapper::toResponse);
    }

    /** APPLICANT: list all own applications */
    @GetMapping("/api/v1/applications/my")
    public List<ApplicationResponse> getMyApplications() {
        return applicationService.getMyApplications().stream().map(applicationWebMapper::toResponse).toList();
    }

    /** RECRUITER / COMPANY: list applications for a vacancy */
    @GetMapping("/api/v1/vacancies/{vacancyId}/applications")
    public PageResponse<ApplicationResponse> getVacancyApplications(
            @PathVariable UUID vacancyId,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        var page = applicationService.getVacancyApplications(vacancyId, PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, applicationWebMapper::toResponse);
    }

    /** RECRUITER / COMPANY: change application status */
    @PutMapping("/api/v1/vacancies/{vacancyId}/applications/{applicationId}/status")
    public ApplicationResponse updateStatus(
            @PathVariable UUID vacancyId,
            @PathVariable UUID applicationId,
            @Valid @RequestBody UpdateApplicationStatusRequest request
    ) {
        return applicationWebMapper.toResponse(
                applicationService.updateStatus(vacancyId, applicationId, request.status()));
    }
}
