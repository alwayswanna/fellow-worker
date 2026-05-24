package a.gleb.vacancy_app.adapter.in.web.mapper;

import a.gleb.vacancy_app.adapter.in.web.dto.ApplicationResponse;
import a.gleb.vacancy_app.domain.model.Application;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class ApplicationWebMapper {

    public ApplicationResponse toResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .vacancyId(application.getVacancyId())
                .applicantAccountId(application.getApplicantAccountId())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt() != null ? application.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(application.getUpdatedAt() != null ? application.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }
}
