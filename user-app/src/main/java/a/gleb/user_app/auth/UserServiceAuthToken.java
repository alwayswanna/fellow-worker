package a.gleb.user_app.auth;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

@Getter
@Setter
public class UserServiceAuthToken extends AbstractAuthenticationToken {

    private String login;
    private Jwt jwt;

    public UserServiceAuthToken(@Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public @Nullable Object getCredentials() {
        return login;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return login != null ? login : jwt;
    }

    @Override
    public String getName() {
        return login;
    }

    @Override
    public boolean isAuthenticated() {
        return Boolean.TRUE;
    }
}
