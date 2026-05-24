package a.gleb.vacancy_app.adapter.out.persistence.mapper;

import a.gleb.vacancy_app.adapter.out.persistence.entity.ApplicationEntity;
import a.gleb.vacancy_app.domain.model.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPersistenceMapper {

    public ApplicationEntity toEntity(Application application) {
        if (application == null) {
            return null;
        }
        return ApplicationEntity.builder()
                .id(application.getId())
                .vacancyId(application.getVacancyId())
                .applicantAccountId(application.getApplicantAccountId())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

    public Application toDomain(ApplicationEntity entity) {
        if (entity == null) {
            return null;
        }
        return Application.builder()
                .id(entity.getId())
                .vacancyId(entity.getVacancyId())
                .applicantAccountId(entity.getApplicantAccountId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
