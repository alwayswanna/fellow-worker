package a.gleb.vacancy_app.application.port.out;

import java.util.UUID;

/**
 * Looks up resume-app to check whether an applicant has at least one resume before letting them
 * apply to a vacancy.
 */
public interface ResumeLookupPort {

    /**
     * Must degrade gracefully (allow the application through) rather than block every applicant
     * when resume-app can't be reached — unlike {@link CompanyLookupPort#checkAssociation}, this
     * isn't a security boundary, just a business precondition, so an outage in resume-app
     * shouldn't take down the core "apply to vacancy" workflow.
     */
    boolean hasResume(UUID accountId);
}