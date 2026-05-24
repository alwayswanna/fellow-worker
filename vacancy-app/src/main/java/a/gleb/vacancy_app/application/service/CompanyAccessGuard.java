package a.gleb.vacancy_app.application.service;

import a.gleb.vacancy_app.application.port.out.CompanyLookupPort;
import a.gleb.vacancy_app.application.port.out.CurrentAccountPort;
import a.gleb.vacancy_app.domain.exception.CompanyNotFoundException;
import a.gleb.vacancy_app.domain.exception.VacancyAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Authorizes vacancy/application management: only the owner or a recruiter of the posting
 * company may manage its vacancies and applications, except platform admins who can manage any
 * company's.
 */
@Component
@RequiredArgsConstructor
public class CompanyAccessGuard {

    private final CompanyLookupPort companyLookupPort;
    private final CurrentAccountPort currentAccountPort;

    public void assertCanManage(UUID companyId, String resourceDescription) {
        if (currentAccountPort.isAdmin()) {
            return;
        }

        var accountId = currentAccountPort.requiredAccountId();
        switch (companyLookupPort.checkAssociation(companyId, accountId)) {
            case ASSOCIATED -> { /* allowed */ }
            case COMPANY_NOT_FOUND -> throw new CompanyNotFoundException("Company not found");
            case NOT_ASSOCIATED -> throw new VacancyAccessDeniedException(
                    "Only the company owner or a recruiter of this company can manage " + resourceDescription);
        }
    }
}
