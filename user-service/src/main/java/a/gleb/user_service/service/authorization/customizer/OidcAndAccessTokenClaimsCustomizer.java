package a.gleb.user_service.service.authorization.customizer;

import a.gleb.user_service.db.entity.AccountEntity;
import a.gleb.user_service.db.entity.RoleEntity;
import a.gleb.user_service.db.entity.authorization.AuthorizationClientEntity;
import a.gleb.user_service.db.repository.AccountRepository;
import a.gleb.user_service.db.repository.authorization.AuthorizationClientRepository;
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

import static a.gleb.user_service.constant.OAuth2ServerConstants.ROLES_CLAIM;

@Slf4j
public record OidcAndAccessTokenClaimsCustomizer(
        AccountRepository accountRepository,
        AuthorizationClientRepository authorizationClientRepository
) implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        log.debug("Customize token");

        if (context.getAuthorizationGrantType().equals(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            var usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) context.getPrincipal();
            var principal = (User) usernamePasswordAuthenticationToken.getPrincipal();
            var username = principal.getUsername();

            var roleCodes = accountRepository.findByUsername(username)
                    .map(AccountEntity::getRole)
                    .map(RoleEntity::getRoleName)
                    .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User does not exists."));

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
