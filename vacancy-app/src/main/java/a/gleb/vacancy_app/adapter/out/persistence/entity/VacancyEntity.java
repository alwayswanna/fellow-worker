package a.gleb.vacancy_app.adapter.out.persistence.entity;

import a.gleb.vacancy_app.domain.model.enums.EmploymentType;
import a.gleb.vacancy_app.domain.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.domain.model.enums.VacancyStatus;
import a.gleb.vacancy_app.domain.model.enums.WorkFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "vacancy", schema = "vacancy")
public class VacancyEntity extends BaseEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "salary_from")
    private Long salaryFrom;

    @Column(name = "salary_to")
    private Long salaryTo;

    @Column(name = "currency", length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 30)
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_format", length = 20)
    private WorkFormat workFormat;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 20)
    private ExperienceLevel experienceLevel;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VacancyStatus status;

    @Column(name = "contact_name", length = 200)
    private String contactName;

    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    // Lazy skills are still read per-row when mapping a search page to the domain model; @BatchSize
    // turns that into one extra "IN (...)" query for the whole page instead of one per vacancy.
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VacancySkillEntity> skills = new ArrayList<>();

    @PrePersist
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @PreUpdate
    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}
