package a.gleb.vacancy_app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedVacancy {

    private UUID id;
    private UUID vacancyId;
    private UUID accountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
