package a.gleb.user_app.adapter.out.security;

import a.gleb.user_app.adapter.in.security.auth.UserServiceAuthToken;
import a.gleb.user_app.application.port.out.CurrentUserPort;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class SpringSecurityCurrentUserAdapter implements CurrentUserPort {

    @Override
    public String requireCurrentLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserServiceAuthToken jwtAuth) {
            var login = jwtAuth.getLogin();
            if (StringUtils.isEmpty(login)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `role` claim in JWT");
            }
            return login;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    @Override
    public String getCurrentLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserServiceAuthToken jwtAuth) {
            return jwtAuth.getLogin();
        }

        return StringUtils.EMPTY;
    }
}
