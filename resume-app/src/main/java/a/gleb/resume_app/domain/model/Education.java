package a.gleb.resume_app.domain.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record Education(
        String institution,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate
) {
}
