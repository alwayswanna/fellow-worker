package a.gleb.resume_app.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "work_experience", schema = "resume")
public class WorkExperienceEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resume_id", nullable = false)
    private ResumeEntity resume;

    @Column(name = "company", nullable = false, length = 150)
    private String company;

    @Column(name = "position", nullable = false, length = 150)
    private String position;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
