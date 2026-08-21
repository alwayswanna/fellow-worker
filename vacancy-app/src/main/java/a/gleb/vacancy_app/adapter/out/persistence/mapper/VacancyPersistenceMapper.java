package a.gleb.vacancy_app.adapter.out.persistence.mapper;

import a.gleb.vacancy_app.adapter.out.persistence.entity.VacancyEntity;
import a.gleb.vacancy_app.adapter.out.persistence.entity.VacancySkillEntity;
import a.gleb.vacancy_app.domain.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyPersistenceMapper {

    public VacancyEntity toEntity(Vacancy vacancy) {
        if (vacancy == null) {
            return null;
        }
        return VacancyEntity.builder()
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
                .contactName(vacancy.getContactName())
                .contactEmail(vacancy.getContactEmail())
                .contactPhone(vacancy.getContactPhone())
                .createdAt(vacancy.getCreatedAt())
                .updatedAt(vacancy.getUpdatedAt())
                .createdBy(vacancy.getCreatedBy())
                .updatedBy(vacancy.getUpdatedBy())
                .build();
    }

    public List<VacancySkillEntity> toSkillEntities(List<String> skills, VacancyEntity vacancy) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .map(name -> VacancySkillEntity.builder().vacancy(vacancy).name(name).build())
                .toList();
    }

    public Vacancy toDomain(VacancyEntity entity) {
        return toDomain(entity, entity.getSkills());
    }

    public Vacancy toDomain(VacancyEntity entity, List<VacancySkillEntity> skills) {
        if (entity == null) {
            return null;
        }
        return Vacancy.builder()
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
                .contactName(entity.getContactName())
                .contactEmail(entity.getContactEmail())
                .contactPhone(entity.getContactPhone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
