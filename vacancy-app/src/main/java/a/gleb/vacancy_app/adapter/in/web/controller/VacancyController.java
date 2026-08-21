package a.gleb.vacancy_app.adapter.in.web.controller;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.vacancy_app.adapter.in.web.PageQueryWebMapper;
import a.gleb.vacancy_app.adapter.in.web.advice.PageResponseMapper;
import a.gleb.vacancy_app.adapter.in.web.controller.docs.VacancySwagger;
import a.gleb.vacancy_app.adapter.in.web.dto.VacancyRequest;
import a.gleb.vacancy_app.adapter.in.web.dto.VacancyResponse;
import a.gleb.vacancy_app.adapter.in.web.mapper.VacancyWebMapper;
import a.gleb.vacancy_app.application.service.VacancyService;
import a.gleb.vacancy_app.domain.model.enums.EmploymentType;
import a.gleb.vacancy_app.domain.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.domain.model.enums.VacancyStatus;
import a.gleb.vacancy_app.domain.model.enums.WorkFormat;
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
    private final VacancyWebMapper vacancyWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponse create(@Valid @RequestBody VacancyRequest request) {
        return vacancyWebMapper.toResponse(vacancyService.create(vacancyWebMapper.toDomain(request)));
    }

    @Override
    @GetMapping("/{id}")
    public VacancyResponse findById(@PathVariable UUID id) {
        return vacancyWebMapper.toResponse(vacancyService.findById(id));
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
        var filter = vacancyWebMapper.toFilter(title, companyId, city, employmentType, workFormat,
                experienceLevel, salaryFrom, salaryTo, status, skills);
        var page = vacancyService.search(filter, PageQueryWebMapper.from(pageable));
        return PageResponseMapper.from(page, vacancyWebMapper::toResponse);
    }

    @Override
    @PutMapping("/{id}")
    public VacancyResponse update(@PathVariable UUID id, @Valid @RequestBody VacancyRequest request) {
        return vacancyWebMapper.toResponse(vacancyService.update(id, vacancyWebMapper.toDomain(request)));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        vacancyService.delete(id);
    }
}
