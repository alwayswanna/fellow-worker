package a.gleb.vacancy_app.application.port.out;

import java.util.UUID;

public interface CurrentAccountPort {

    /**
     * @throws org.springframework.web.server.ResponseStatusException 401 if missing/unauthenticated.
     */
    UUID requiredAccountId();

    /**
     * True if the current account's JWT `role` claim is the platform admin role — admins manage
     * any company's vacancies/applications, bypassing the owner/recruiter association check.
     */
    boolean isAdmin();
}
