package a.gleb.resume_app.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "resume", schema = "resume")
public class ResumeEntity extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "desired_position", length = 100)
    private String desiredPosition;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Setter
    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    // Lazy collections are read per-row when mapping a list (findAll/findAllByAccountId) to the
    // domain model; @BatchSize turns that into one extra "IN (...)" query per collection for the
    // whole page/list instead of one per resume per collection.
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WorkExperienceEntity> workExperiences = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EducationEntity> educations = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SkillEntity> skills = new ArrayList<>();

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ResumeExternalLinkEntity> links = new ArrayList<>();

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
