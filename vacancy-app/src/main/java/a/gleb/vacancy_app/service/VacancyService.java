package a.gleb.vacancy_app.service;

import a.gleb.vacancy_app.db.repository.VacancyRepository;
import a.gleb.vacancy_app.db.repository.VacancySkillRepository;
import a.gleb.vacancy_app.db.spec.VacancySpecification;
import a.gleb.vacancy_app.mapper.VacancyMapper;
import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import a.gleb.vacancy_app.model.request.VacancyRequest;
import a.gleb.vacancy_app.model.response.VacancyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancySkillRepository skillRepository;
    private final VacancyMapper mapper;

    @Transactional
    public VacancyResponse create(VacancyRequest request) {
        var vacancy = vacancyRepository.save(mapper.toEntity(request));
        var skills = skillRepository.saveAll(mapper.toSkillEntities(request.skills(), vacancy));
        return mapper.toResponse(vacancy, skills);
    }

    @Transactional(readOnly = true)
    public VacancyResponse findById(UUID id) {
        return vacancyRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacancy not found"));
    }

    @Transactional(readOnly = true)
    public Page<VacancyResponse> search(
            String title,
            UUID companyId,
            String city,
            EmploymentType employmentType,
            WorkFormat workFormat,
            ExperienceLevel experienceLevel,
            Long salaryFrom,
            Long salaryTo,
            VacancyStatus status,
            List<String> skills,
            Pageable pageable
    ) {
        var spec = VacancySpecification.build(
                title, companyId, city, employmentType, workFormat,
                experienceLevel, salaryFrom, salaryTo, status, skills
        );
        return vacancyRepository.findAll(spec, pageable).map(mapper::toResponse);
    }

    @Transactional
    public VacancyResponse update(UUID id, VacancyRequest request) {
        var existing = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacancy not found"));

        skillRepository.deleteAllByVacancyId(id);
        var updated = vacancyRepository.save(mapper.toEntity(existing, request));
        var skills = skillRepository.saveAll(mapper.toSkillEntities(request.skills(), updated));

        return mapper.toResponse(updated, skills);
    }

    @Transactional
    public void delete(UUID id) {
        if (!vacancyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacancy not found");
        }
        vacancyRepository.deleteById(id);
    }
}
