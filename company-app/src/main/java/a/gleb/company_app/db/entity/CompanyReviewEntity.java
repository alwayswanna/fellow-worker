package a.gleb.company_app.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(
        name = "company_review",
        schema = "company",
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "account_id"})
)
public class CompanyReviewEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

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
