package a.gleb.vacancy_app.controller;

import a.gleb.vacancy_app.controller.docs.VacancySwagger;
import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import a.gleb.vacancy_app.model.request.VacancyRequest;
import a.gleb.vacancy_app.model.response.VacancyResponse;
import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.vacancy_app.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vacancies")
public class VacancyController implements VacancySwagger {

    private final VacancyService vacancyService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponse create(@Valid @RequestBody VacancyRequest request) {
        return vacancyService.create(request);
    }

    @Override
    @GetMapping("/{id}")
    public VacancyResponse findById(@PathVariable UUID id) {
        return vacancyService.findById(id);
    }

    @Override
    @GetMapping
    public PageResponse<VacancyResponse> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) EmploymentType employmentType,
            @RequestParam(required = false) WorkFormat workFormat,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) Long salaryFrom,
            @RequestParam(required = false) Long salaryTo,
            @RequestParam(required = false, defaultValue = "ACTIVE") VacancyStatus status,
            @RequestParam(required = false) List<String> skills,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        var p = vacancyService.search(title, companyId, city, employmentType, workFormat,
                experienceLevel, salaryFrom, salaryTo, status, skills, pageable);
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    @PutMapping("/{id}")
    public VacancyResponse update(@PathVariable UUID id, @Valid @RequestBody VacancyRequest request) {
        return vacancyService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        vacancyService.delete(id);
    }
}
