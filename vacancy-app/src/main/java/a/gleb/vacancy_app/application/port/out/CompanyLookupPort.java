package a.gleb.vacancy_app.application.port.out;

import a.gleb.vacancy_app.domain.model.CompanySummary;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Looks up company-app for the posting company's display info. Implementations
 * must degrade gracefully (omit the id from the result) rather than throw when
 * company-app is unreachable or a given id is unknown — a missing company
 * summary just falls back to the vacancy's bare companyId on the frontend.
 */
public interface CompanyLookupPort {

    Map<UUID, CompanySummary> findAllById(Collection<UUID> companyIds);
}
