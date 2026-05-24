package a.gleb.resume_app.adapter.out.security;

import a.gleb.resume_app.application.port.out.CurrentAccountPort;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Identity приходит из доверенных заголовков, проставленных gateway после валидации JWT
 * ({@code AuthorizationFilter}) — сервис JWT сам не декодирует и не проверяет. Это безопасно
 * только потому, что сервис физически недостижим иначе как через gateway.
 */
@Component
@RequiredArgsConstructor
public class AccountAdapter implements CurrentAccountPort {

    private static final String ACCOUNT_ID_HEADER = "X-Account-Id";
    private static final String ROLE_HEADER = "X-Role";
    private static final String LOGIN_HEADER = "X-Login";

    private final HttpServletRequest request;

    @Override
    public UUID requiredAccountId() {
        var accountId = request.getHeader(ACCOUNT_ID_HEADER);
        if (accountId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `%s` header".formatted(ACCOUNT_ID_HEADER));
        }
        return UUID.fromString(accountId);
    }

    @Override
    public String requiredRole() {
        var role = request.getHeader(ROLE_HEADER);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `%s` header".formatted(ROLE_HEADER));
        }
        return role;
    }

    /**
     * Used directly by {@link a.gleb.resume_app.config.filter.RequestLoggingFilter} — a pure web
     * infrastructure concern, so it bypasses the {@link CurrentAccountPort} abstraction.
     */
    public String getAccountLogin() {
        var login = request.getHeader(LOGIN_HEADER);
        if (StringUtils.isEmpty(login)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `%s` header".formatted(LOGIN_HEADER));
        }
        return login;
    }
}
