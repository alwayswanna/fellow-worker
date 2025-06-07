package a.gleb.oauth2server.service.authorization.customizer;

import a.gleb.oauth2server.db.entity.AccountEntity;
import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.db.entity.authorization.AuthorizationClientEntity;
import a.gleb.oauth2server.db.repository.AccountRepository;
import a.gleb.oauth2server.db.repository.authorization.AuthorizationClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Optional;
import java.util.function.Function;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.ROLES_CLAIM;

@Slf4j
@RequiredArgsConstructor
public class OidcAndAccessTokenClaimsCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final AccountRepository accountRepository;
    private final AuthorizationClientRepository authorizationClientRepository;

    @Override
    public void customize(JwtEncodingContext context) {
        log.debug("Customize token");

        if (context.getAuthorizationGrantType().equals(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            var usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) context.getPrincipal();
            var principal = (User) usernamePasswordAuthenticationToken.getPrincipal();
            var username = principal.getUsername();

            var roleCodes = accountRepository.findByUsername(username)
                    .map(AccountEntity::getRoles)
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User does not exists."))
                    .stream()
                    .map(RoleEntity::getRoleName)
                    .toList();

            context.getClaims().claim("roles", roleCodes);
        } else {
            var principal = (OAuth2ClientAuthenticationToken) context.getPrincipal();

            if (principal == null) {
                throw new AuthenticationCredentialsNotFoundException("Nullable principal does not allowed.");
            }

            var roleCodes = Optional.ofNullable(principal.getRegisteredClient())
                    .map(it -> authorizationClientRepository.findAuthorizationClientEntityByClientId(it.getClientId()))
                    .flatMap(Function.identity())
                    .map(AuthorizationClientEntity::getRoles)
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Client does not exists."))
                    .stream()
                    .map(RoleEntity::getRoleName)
                    .toList();

            context.getClaims().claim(ROLES_CLAIM, roleCodes);
        }
    }
}
