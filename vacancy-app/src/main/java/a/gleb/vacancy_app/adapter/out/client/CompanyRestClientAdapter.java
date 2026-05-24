package a.gleb.vacancy_app.adapter.out.client;

import a.gleb.vacancy_app.application.port.out.CompanyLookupPort;
import a.gleb.vacancy_app.domain.model.CompanyAssociation;
import a.gleb.vacancy_app.domain.model.CompanySummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Talks to company-app's company/recruiter endpoints — enriches vacancy responses with display
 * data (name/logo/city/rating) and authorizes vacancy/application management by checking whether
 * an account owns or recruits at a company.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyRestClientAdapter implements CompanyLookupPort {

    private final RestClient companyServiceRestClient;

    @Override
    public Map<UUID, CompanySummary> findAllById(Collection<UUID> companyIds) {
        if (companyIds.isEmpty()) {
            return Map.of();
        }
        try {
            List<CompanyClientResponse> companies = companyServiceRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/companies/batch")
                            .queryParam("ids", companyIds)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CompanyClientResponse>>() {});
            return (companies == null ? List.<CompanyClientResponse>of() : companies).stream()
                    .collect(Collectors.toMap(CompanyClientResponse::id, CompanyClientResponse::toSummary));
        } catch (RestClientException e) {
            log.warn("CompanyRestClientAdapter: failed to batch-fetch companies, vacancies will show " +
                    "them as unknown [companyIds={}, error={}]", companyIds, e.getMessage());
            return Map.of();
        }
    }

    @Override
    public CompanyAssociation checkAssociation(UUID companyId, UUID accountId) {
        CompanyClientResponse company;
        try {
            company = companyServiceRestClient.get()
                    .uri("/api/v1/companies/{id}", companyId)
                    .retrieve()
                    .body(CompanyClientResponse.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return CompanyAssociation.COMPANY_NOT_FOUND;
            }
            log.warn("CompanyRestClientAdapter: failed to verify company for access check, denying " +
                    "by default [companyId={}, error={}]", companyId, e.getMessage());
            return CompanyAssociation.NOT_ASSOCIATED;
        } catch (RestClientException e) {
            log.warn("CompanyRestClientAdapter: company-app unreachable during access check, denying " +
                    "by default [companyId={}, error={}]", companyId, e.getMessage());
            return CompanyAssociation.NOT_ASSOCIATED;
        }

        if (company == null) {
            return CompanyAssociation.COMPANY_NOT_FOUND;
        }
        if (accountId.equals(company.ownerAccountId())) {
            return CompanyAssociation.ASSOCIATED;
        }

        try {
            companyServiceRestClient.get()
                    .uri("/api/v1/companies/{companyId}/recruiters/{accountId}", companyId, accountId)
                    .retrieve()
                    .toBodilessEntity();
            return CompanyAssociation.ASSOCIATED;
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() != 404) {
                log.warn("CompanyRestClientAdapter: failed to verify recruiter membership, denying by " +
                        "default [companyId={}, accountId={}, error={}]", companyId, accountId, e.getMessage());
            }
            return CompanyAssociation.NOT_ASSOCIATED;
        } catch (RestClientException e) {
            log.warn("CompanyRestClientAdapter: company-app unreachable verifying recruiter membership, " +
                    "denying by default [companyId={}, accountId={}, error={}]", companyId, accountId, e.getMessage());
            return CompanyAssociation.NOT_ASSOCIATED;
        }
    }

    private record CompanyClientResponse(
            UUID id,
            UUID ownerAccountId,
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
