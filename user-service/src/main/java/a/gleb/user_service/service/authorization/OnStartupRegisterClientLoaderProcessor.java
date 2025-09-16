package a.gleb.user_service.service.authorization;

import a.gleb.user_service.configuration.properties.OAuth2ServerConfigurationProperties;
import a.gleb.user_service.configuration.properties.OAuth2ServerConfigurationProperties.DefaultClient;
import a.gleb.user_service.configuration.properties.OAuth2ServerConfigurationProperties.DefaultRole;
import a.gleb.user_service.db.entity.RoleEntity;
import a.gleb.user_service.db.repository.RoleRepository;
import a.gleb.user_service.service.authorization.dao.JpaRegisteredClientRepository;
import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.*;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.OPENID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "user-service.initialization-on-startup.enabled", havingValue = "true")
public class OnStartupRegisterClientLoaderProcessor implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2ServerConfigurationProperties properties;
    private final JpaRegisteredClientRepository jpaRegisteredClientRepository;

    /**
     * Load clients from config, if client does not exist id database -> save it.
     */
    @Override
    public void run(ApplicationArguments args) {
        var rolesToAttractToDefaultClient = properties.defaultRoles()
                .stream()
                .filter(it -> !roleRepository.existsByRoleName(it.roleName()))
                .map(it -> Pair.of(it, toRole(it)))
                .map(it -> Pair.of(it.getLeft(), roleRepository.save(it.getRight())))
                .filter(it -> it.getLeft().isMapOnDefaultClient())
                .map(Pair::getRight)
                .collect(Collectors.toSet());

        properties.defaultClients()
                .values()
                .stream()
                .map(this::toClient)
                .forEach(it -> this.save(it, rolesToAttractToDefaultClient));
    }

    private RoleEntity toRole(DefaultRole defaultRole) {
        return RoleEntity.builder()
                .roleName(defaultRole.roleName())
                .systemRole(defaultRole.systemRole())
                .displayName(defaultRole.displayName())
                .build();
    }

    private RegisteredClient toClient(DefaultClient clientFromConfig) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .tokenSettings(toTokenSettings(clientFromConfig))
                .clientId(clientFromConfig.clientId())
                .clientSecret(passwordEncoder.encode(clientFromConfig.clientSecret()))
                .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(CLIENT_SECRET_POST)
                .clientAuthenticationMethod(CLIENT_SECRET_JWT)
                .clientAuthenticationMethod(PRIVATE_KEY_JWT)
                .clientAuthenticationMethod(NONE)
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(Instant.now().plus(clientFromConfig.clientSecretTtl(), DAYS))
                .authorizationGrantType(AUTHORIZATION_CODE)
                .authorizationGrantType(REFRESH_TOKEN)
                .authorizationGrantType(CLIENT_CREDENTIALS)
                .redirectUris(redirectConf -> redirectConf.addAll(clientFromConfig.redirectUris()))
                .scope(OPENID)
                .build();
    }

    private static TokenSettings toTokenSettings(DefaultClient clientFromConfig) {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(clientFromConfig.accessTokenTtl()))
                .refreshTokenTimeToLive(Duration.ofDays(clientFromConfig.refreshTokenTtl()))
                .build();
    }

    private void save(RegisteredClient client, Set<RoleEntity> roleEntities) {
        if (!jpaRegisteredClientRepository.existByClientId(client.getClientId())) {
            jpaRegisteredClientRepository.saveWithRoles(client, roleEntities);
        }
    }
}
