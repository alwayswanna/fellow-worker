package a.gleb.vacancy_app.domain.model;

import a.gleb.vacancy_app.domain.model.enums.EmploymentType;
import a.gleb.vacancy_app.domain.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.domain.model.enums.VacancyStatus;
import a.gleb.vacancy_app.domain.model.enums.WorkFormat;

import java.util.List;
import java.util.UUID;

public record VacancySearchFilter(
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
}
