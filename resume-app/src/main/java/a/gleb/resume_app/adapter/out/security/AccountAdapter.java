package a.gleb.resume_app.adapter.out.security;

import a.gleb.resume_app.application.port.out.CurrentAccountPort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AccountAdapter implements CurrentAccountPort {

    @Override
    public UUID requiredAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String accountId = jwtAuth.getToken().getClaimAsString("account_id");
            if (accountId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `account_id` claim in JWT");
            }
            return UUID.fromString(accountId);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    /**
     * Used directly by {@link a.gleb.resume_app.config.filter.RequestLoggingFilter} — a pure web
     * infrastructure concern, so it bypasses the {@link CurrentAccountPort} abstraction.
     */
    public String getAccountLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var login = jwtAuth.getToken().getClaimAsString("login");
            if (StringUtils.isEmpty(login)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `role` claim in JWT");
            }
            return login;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }
}
