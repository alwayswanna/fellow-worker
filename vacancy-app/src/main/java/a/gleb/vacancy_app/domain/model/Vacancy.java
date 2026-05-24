package a.gleb.vacancy_app.domain.model;

import a.gleb.vacancy_app.domain.model.enums.EmploymentType;
import a.gleb.vacancy_app.domain.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.domain.model.enums.VacancyStatus;
import a.gleb.vacancy_app.domain.model.enums.WorkFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vacancy {

    private UUID id;
    private UUID companyId;
    private String title;
    private String description;
    private String requirements;
    private Long salaryFrom;
    private Long salaryTo;
    private String currency;
    private EmploymentType employmentType;
    private WorkFormat workFormat;
    private ExperienceLevel experienceLevel;
    private String city;
    private String country;
    private VacancyStatus status;
    private List<String> skills;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
