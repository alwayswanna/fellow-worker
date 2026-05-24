package a.gleb.vacancy_app.adapter.out.persistence.mapper;

import a.gleb.vacancy_app.adapter.out.persistence.entity.SavedVacancyEntity;
import a.gleb.vacancy_app.domain.model.SavedVacancy;
import org.springframework.stereotype.Component;

@Component
public class SavedVacancyPersistenceMapper {

    public SavedVacancyEntity toEntity(SavedVacancy savedVacancy) {
        if (savedVacancy == null) {
            return null;
        }
        return SavedVacancyEntity.builder()
                .id(savedVacancy.getId())
                .vacancyId(savedVacancy.getVacancyId())
                .accountId(savedVacancy.getAccountId())
                .createdAt(savedVacancy.getCreatedAt())
                .updatedAt(savedVacancy.getUpdatedAt())
                .build();
    }

    public SavedVacancy toDomain(SavedVacancyEntity entity) {
        if (entity == null) {
            return null;
        }
        return SavedVacancy.builder()
                .id(entity.getId())
                .vacancyId(entity.getVacancyId())
                .accountId(entity.getAccountId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
