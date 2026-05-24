package a.gleb.vacancy_app.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "saved_vacancy", schema = "vacancy")
public class SavedVacancyEntity extends BaseEntity {

    @Column(name = "vacancy_id", nullable = false)
    private UUID vacancyId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

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
