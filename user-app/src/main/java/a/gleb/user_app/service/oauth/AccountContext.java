package a.gleb.user_app.service.oauth;

import a.gleb.user_app.auth.UserServiceAuthToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AccountContext {

    public String requiredAccountLogin() {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserServiceAuthToken jwtAuth) {
            var login = jwtAuth.getLogin();
            if (StringUtils.isEmpty(login)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing `role` claim in JWT");
            }
            return login;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    public String getAccountLogin() {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserServiceAuthToken jwtAuth) {
            return jwtAuth.getLogin();
        }

        return StringUtils.EMPTY;
    }
}
