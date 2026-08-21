package a.gleb.vacancy_app.adapter.in.web.mapper;

import a.gleb.vacancy_app.adapter.in.web.dto.VacancyRequest;
import a.gleb.vacancy_app.adapter.in.web.dto.VacancyResponse;
import a.gleb.vacancy_app.domain.model.Vacancy;
import a.gleb.vacancy_app.domain.model.VacancySearchFilter;
import a.gleb.vacancy_app.domain.model.enums.EmploymentType;
import a.gleb.vacancy_app.domain.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.domain.model.enums.VacancyStatus;
import a.gleb.vacancy_app.domain.model.enums.WorkFormat;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class VacancyWebMapper {

    public Vacancy toDomain(VacancyRequest request) {
        return Vacancy.builder()
                .companyId(request.companyId())
                .title(request.title())
                .description(request.description())
                .requirements(request.requirements())
                .salaryFrom(request.salaryFrom())
                .salaryTo(request.salaryTo())
                .currency(request.currency())
                .employmentType(request.employmentType())
                .workFormat(request.workFormat())
                .experienceLevel(request.experienceLevel())
                .city(request.city())
                .country(request.country())
                .status(request.status())
                .skills(request.skills())
                .contactName(request.contactName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .build();
    }

    public VacancySearchFilter toFilter(
            String title,
            UUID companyId,
            String city,
            EmploymentType employmentType,
            WorkFormat workFormat,
            ExperienceLevel experienceLevel,
            Long salaryFrom,
            Long salaryTo,
            VacancyStatus status,
            List<String> skills
    ) {
        return new VacancySearchFilter(title, companyId, city, employmentType, workFormat,
                experienceLevel, salaryFrom, salaryTo, status, skills);
    }

    public VacancyResponse toResponse(Vacancy vacancy) {
        return VacancyResponse.builder()
                .id(vacancy.getId())
                .companyId(vacancy.getCompanyId())
                .title(vacancy.getTitle())
                .description(vacancy.getDescription())
                .requirements(vacancy.getRequirements())
                .salaryFrom(vacancy.getSalaryFrom())
                .salaryTo(vacancy.getSalaryTo())
                .currency(vacancy.getCurrency())
                .employmentType(vacancy.getEmploymentType())
                .workFormat(vacancy.getWorkFormat())
                .experienceLevel(vacancy.getExperienceLevel())
                .city(vacancy.getCity())
                .country(vacancy.getCountry())
                .status(vacancy.getStatus())
                .skills(vacancy.getSkills())
                .createdAt(vacancy.getCreatedAt() != null ? vacancy.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(vacancy.getUpdatedAt() != null ? vacancy.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .contactName(vacancy.getContactName())
                .contactEmail(vacancy.getContactEmail())
                .contactPhone(vacancy.getContactPhone())
                .build();
    }
}
