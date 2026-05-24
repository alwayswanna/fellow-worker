package a.gleb.vacancy_app.db.entity;

import a.gleb.vacancy_app.model.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "application", schema = "vacancy")
public class ApplicationEntity extends BaseEntity {

    @Column(name = "vacancy_id", nullable = false)
    private UUID vacancyId;

    @Column(name = "applicant_account_id", nullable = false)
    private UUID applicantAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status;

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

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
