package a.gleb.vacancy_app.adapter.out.client;

import a.gleb.vacancy_app.application.port.out.CompanyLookupPort;
import a.gleb.vacancy_app.domain.model.CompanySummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Enriches vacancies with company display data by calling company-app's
 * existing {@code GET /api/v1/companies/{id}} endpoint — the same one the
 * frontend already uses for the "view company" action. There is no batch
 * endpoint, so each distinct id in a result page is fetched individually;
 * a failure on any single id is logged and simply omitted rather than
 * failing the whole vacancy search/detail request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyRestClientAdapter implements CompanyLookupPort {

    private final RestClient companyServiceRestClient;

    @Override
    public Map<UUID, CompanySummary> findAllById(Collection<UUID> companyIds) {
        Map<UUID, CompanySummary> result = new HashMap<>();
        for (UUID id : new LinkedHashSet<>(companyIds)) {
            fetch(id).ifPresent(summary -> result.put(id, summary));
        }
        return result;
    }

    private Optional<CompanySummary> fetch(UUID id) {
        try {
            CompanyClientResponse response = companyServiceRestClient.get()
                    .uri("/api/v1/companies/{id}", id)
                    .retrieve()
                    .body(CompanyClientResponse.class);
            return Optional.ofNullable(response).map(CompanyClientResponse::toSummary);
        } catch (RestClientException e) {
            log.warn("CompanyRestClientAdapter: failed to fetch company, vacancies will show it as unknown " +
                    "[companyId={}, error={}]", id, e.getMessage());
            return Optional.empty();
        }
    }

    private record CompanyClientResponse(
            UUID id,
            String name,
            String logoUrl,
            String city,
            double rating
    ) {
        CompanySummary toSummary() {
            return new CompanySummary(id, name, logoUrl, city, rating);
        }
    }
}
