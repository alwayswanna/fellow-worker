package a.gleb.vacancy_app.application.port.out;

import a.gleb.vacancy_app.domain.model.CompanyAssociation;
import a.gleb.vacancy_app.domain.model.CompanySummary;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Looks up company-app for the posting company's display info and ownership.
 */
public interface CompanyLookupPort {

    /**
     * Must degrade gracefully (omit the id from the result) rather than throw when company-app is
     * unreachable or a given id is unknown — a missing company summary just falls back to the
     * vacancy's bare companyId on the frontend. Used on the hot read path (vacancy search/detail),
     * so failures must never block the response.
     */
    Map<UUID, CompanySummary> findAllById(Collection<UUID> companyIds);

    /**
     * Used to authorize vacancy/application management. Unlike {@link #findAllById}, this must
     * fail CLOSED: if company-app can't be reached or the check can't be completed, treat it as
     * {@link CompanyAssociation#NOT_ASSOCIATED} rather than granting access.
     */
    CompanyAssociation checkAssociation(UUID companyId, UUID accountId);
}
