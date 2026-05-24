package a.gleb.company_app.db.entity;

import a.gleb.company_app.model.enums.CompanySize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "company", schema = "company")
public class CompanyEntity extends BaseEntity {

    @Column(name = "owner_account_id", nullable = false, unique = true)
    private UUID ownerAccountId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "website", length = 500)
    private String website;

    @Setter
    @Column(name = "logo_url", length = 1000)
    private String logoUrl;

    @Column(name = "industry", length = 100)
    private String industry;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", length = 20)
    private CompanySize size;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Setter
    @Column(name = "rating", nullable = false)
    private double rating;

    @Setter
    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CompanyRecruiterEntity> recruiters = new ArrayList<>();

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
