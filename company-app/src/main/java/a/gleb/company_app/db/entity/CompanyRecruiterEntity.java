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
        name = "company_recruiter",
        schema = "company",
        uniqueConstraints = @UniqueConstraint(columnNames = "account_id")
)
public class CompanyRecruiterEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @PrePersist
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
