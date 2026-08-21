package a.gleb.resume_app.domain.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record WorkExperience(
        String company,
        String position,
        LocalDate startDate,
        LocalDate endDate,
        String description
) {
}
