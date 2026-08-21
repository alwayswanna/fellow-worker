package a.gleb.company_app.application.port.out;

import java.util.UUID;

public interface CurrentAccountPort {

    /**
     * @throws org.springframework.web.server.ResponseStatusException 401 if missing/unauthenticated.
     */
    UUID requiredAccountId();
}
