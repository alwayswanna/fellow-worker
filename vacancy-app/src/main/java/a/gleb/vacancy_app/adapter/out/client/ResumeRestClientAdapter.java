package a.gleb.vacancy_app.adapter.out.client;

import a.gleb.vacancy_app.application.port.out.ResumeLookupPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

/**
 * Talks to resume-app's internal `/exists` check to gate vacancy applications on the applicant
 * having at least one resume. Unlike {@link CompanyRestClientAdapter}'s calls, resume-app's
 * `/exists` endpoint is authenticated, so the caller's own identity is forwarded rather than
 * calling in anonymously — as the trusted {@code X-Account-Id}/{@code X-Role}/{@code X-Login}
 * headers set by gateway on the incoming request, not a JWT (this service no longer holds one).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeRestClientAdapter implements ResumeLookupPort {

    private static final String ACCOUNT_ID_HEADER = "X-Account-Id";
    private static final String ROLE_HEADER = "X-Role";
    private static final String LOGIN_HEADER = "X-Login";

    private final RestClient resumeServiceRestClient;
    private final HttpServletRequest request;

    @Override
    public boolean hasResume(UUID accountId) {
        try {
            Boolean exists = resumeServiceRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/resumes/exists")
                            .queryParam("accountId", accountId)
                            .build())
                    .headers(this::forwardIdentityHeaders)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (RestClientException e) {
            log.warn("ResumeRestClientAdapter: resume-app unreachable while checking resume " +
                    "existence, allowing application to proceed [accountId={}, error={}]", accountId, e.getMessage());
            return true;
        }
    }

    private void forwardIdentityHeaders(HttpHeaders headers) {
        forwardIfPresent(headers, ACCOUNT_ID_HEADER);
        forwardIfPresent(headers, ROLE_HEADER);
        forwardIfPresent(headers, LOGIN_HEADER);
    }

    private void forwardIfPresent(HttpHeaders headers, String headerName) {
        var value = request.getHeader(headerName);
        if (value != null) {
            headers.set(headerName, value);
        }
    }
}