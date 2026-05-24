package a.gleb.vacancy_app.domain.model;

import java.util.UUID;

/**
 * Read-only projection of a company, fetched from company-app to enrich
 * vacancy responses (name/logo/city/rating shown on vacancy cards).
 */
public record CompanySummary(
        UUID id,
        String name,
        String logoUrl,
        String city,
        double rating
) {
}
