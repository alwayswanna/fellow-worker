package a.gleb.oauth2server.service.authorization;

import a.gleb.oauth2server.configuration.properties.OAuth2ServerConfigurationProperties;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerConfigurationProperties.DefaultClient;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerConfigurationProperties.DefaultRole;
import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.db.repository.RoleRepository;
import a.gleb.oauth2server.service.authorization.dao.JpaRegisteredClientRepository;
import com.nimbusds.jose.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
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
                .stream()
                .map(this::toClient)
                .forEach(it -> this.save(it, rolesToAttractToDefaultClient));
    }

    private RoleEntity toRole(DefaultRole defaultRole) {
        return RoleEntity.builder()
                .roleName(defaultRole.roleName())
                .displayName(defaultRole.displayName())
                .lastUpdate(LocalDateTime.now())
                .build();
    }

    private RegisteredClient toClient(DefaultClient clientFromConfig) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .tokenSettings(toTokenSettings(clientFromConfig))
                .clientId(clientFromConfig.defaultClientId())
                .clientSecret(passwordEncoder.encode(clientFromConfig.defaultClientSecret()))
                .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(CLIENT_SECRET_POST)
                .clientAuthenticationMethod(CLIENT_SECRET_JWT)
                .clientAuthenticationMethod(PRIVATE_KEY_JWT)
                .clientAuthenticationMethod(NONE)
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(Instant.now().plus(clientFromConfig.clientSecretDaysTtl(), DAYS))
                .authorizationGrantType(AUTHORIZATION_CODE)
                .authorizationGrantType(REFRESH_TOKEN)
                .authorizationGrantType(CLIENT_CREDENTIALS)
                .redirectUris(redirectConf -> redirectConf.addAll(clientFromConfig.defaultRedirectUris()))
                .scope(OPENID)
                .build();
    }

    private static TokenSettings toTokenSettings(DefaultClient clientFromConfig) {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(clientFromConfig.defaultAccessTokenTimeToLive()))
                .refreshTokenTimeToLive(Duration.ofDays(clientFromConfig.defaultRefreshTokenTimeToLive()))
                .build();
    }

    private void save(RegisteredClient client, Set<RoleEntity> roleEntities) {
        if (!jpaRegisteredClientRepository.existByClientId(client.getClientId())) {
            jpaRegisteredClientRepository.saveWithRoles(client, roleEntities);
        }
    }
}
