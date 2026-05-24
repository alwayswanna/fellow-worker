package a.gleb.vacancy_app.adapter.in.web.controller;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.vacancy_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.vacancy_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.vacancy_app.adapter.in.web.dto.VacancyResponse;
import a.gleb.vacancy_app.adapter.in.web.mapper.VacancyWebMapper;
import a.gleb.vacancy_app.application.service.SavedVacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SavedVacancyController {

    private final SavedVacancyService savedVacancyService;
    private final VacancyWebMapper vacancyWebMapper;

    /** APPLICANT: bookmark a vacancy */
    @PostMapping("/api/v1/vacancies/{vacancyId}/save")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@PathVariable UUID vacancyId) {
        savedVacancyService.save(vacancyId);
    }

    /** APPLICANT: remove a bookmark */
    @DeleteMapping("/api/v1/vacancies/{vacancyId}/save")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsave(@PathVariable UUID vacancyId) {
        savedVacancyService.unsave(vacancyId);
    }

    /** APPLICANT: list own bookmarked vacancies */
    @GetMapping("/api/v1/vacancies/saved")
    public PageResponse<VacancyResponse> getSaved(@PageableDefault(size = 20) Pageable pageable) {
        var page = savedVacancyService.getSaved(PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, vacancyWebMapper::toResponse);
    }
}
