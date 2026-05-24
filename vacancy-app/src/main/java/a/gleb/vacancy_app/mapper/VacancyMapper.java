package a.gleb.vacancy_app.mapper;

import a.gleb.vacancy_app.db.entity.VacancyEntity;
import a.gleb.vacancy_app.db.entity.VacancySkillEntity;
import a.gleb.vacancy_app.model.request.VacancyRequest;
import a.gleb.vacancy_app.model.response.VacancyResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class VacancyMapper {

    public VacancyEntity toEntity(VacancyRequest request) {
        return VacancyEntity.builder()
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
                .contactName(request.contactName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .build();
    }

    public VacancyEntity toEntity(VacancyEntity existing, VacancyRequest request) {
        return VacancyEntity.builder()
                .id(existing.getId())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
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
                .contactName(request.contactName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .build();
    }

    public List<VacancySkillEntity> toSkillEntities(List<String> skills, VacancyEntity vacancy) {
        if (skills == null) return List.of();
        return skills.stream()
                .map(name -> VacancySkillEntity.builder().vacancy(vacancy).name(name).build())
                .toList();
    }

    public VacancyResponse toResponse(VacancyEntity entity) {
        return toResponse(entity, entity.getSkills());
    }

    public VacancyResponse toResponse(VacancyEntity entity, List<VacancySkillEntity> skills) {
        return VacancyResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .requirements(entity.getRequirements())
                .salaryFrom(entity.getSalaryFrom())
                .salaryTo(entity.getSalaryTo())
                .currency(entity.getCurrency())
                .employmentType(entity.getEmploymentType())
                .workFormat(entity.getWorkFormat())
                .experienceLevel(entity.getExperienceLevel())
                .city(entity.getCity())
                .country(entity.getCountry())
                .status(entity.getStatus())
                .skills(skills.stream().map(VacancySkillEntity::getName).toList())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .contactName(entity.getContactName())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .build();
    }
}
