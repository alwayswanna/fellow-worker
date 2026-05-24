package a.gleb.user_app.mapper.oauth;

import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.db.entity.oauth.ClientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.oauth2.server.authorization.autoconfigure.servlet.OAuth2AuthorizationServerProperties.Client;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegisteredClientMapper extends AbstractOAuthMapper {

    private final PasswordEncoder passwordEncoder;
    private final UserAppConfigurationProperties properties;

    public ClientEntity toEntity(RegisteredClient registeredClient) {
        return ClientEntity.builder()
                .clientId(registeredClient.getClientId())
                .clientIdIssuedAt(
                        registeredClient.getClientIdIssuedAt() != null
                                ? registeredClient.getClientIdIssuedAt()
                                : LocalDateTime.now().toInstant(ZoneOffset.UTC)
                )
                .clientSecret(registeredClient.getClientSecret() != null
                        ? passwordEncoder.encode(registeredClient.getClientSecret())
                        : null)
                .clientSecretExpiresAt(registeredClient.getClientSecret() == null
                        ? null
                        : registeredClient.getClientSecretExpiresAt() != null
                                ? registeredClient.getClientSecretExpiresAt()
                                : LocalDateTime.now()
                                        .plus(properties.registeredClientDefaultOptions().clientSecretExpiresAt())
                                        .toInstant(ZoneOffset.UTC))
                .clientName(registeredClient.getClientName())
                .clientAuthenticationMethods(registeredClient.getClientAuthenticationMethods().stream()
                        .map(ClientAuthenticationMethod::getValue)
                        .collect(Collectors.joining(",")))
                .authorizationGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                        .map(AuthorizationGrantType::getValue)
                        .collect(Collectors.joining(",")))
                .redirectUris(String.join(",", registeredClient.getRedirectUris()))
                .postLogoutRedirectUris(String.join(",", registeredClient.getPostLogoutRedirectUris()))
                .scopes(String.join(",", registeredClient.getScopes()))
                .clientSettings(writeMap(registeredClient.getClientSettings().getSettings()))
                .tokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()))
                .build();
    }

    public RegisteredClient toRegisteredClient(ClientEntity entity) {
        return RegisteredClient.withId(entity.getId().toString())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt())
                .clientName(entity.getClientName())
                .scopes(s -> s.addAll(parseSet(entity.getScopes())))
                .redirectUris(u -> u.addAll(parseSet(entity.getRedirectUris())))
                .postLogoutRedirectUris(u -> u.addAll(parseSet(entity.getPostLogoutRedirectUris())))
                .clientAuthenticationMethods(m -> parseSet(entity.getClientAuthenticationMethods())
                        .stream().map(ClientAuthenticationMethod::new).forEach(m::add))
                .authorizationGrantTypes(t -> parseSet(entity.getAuthorizationGrantTypes())
                        .stream().map(AuthorizationGrantType::new).forEach(t::add))
                .clientSettings(ClientSettings.withSettings(readMap(entity.getClientSettings())).build())
                .tokenSettings(TokenSettings.withSettings(readMap(entity.getTokenSettings())).build())
                .build();
    }

    public RegisteredClient toRegisteredClient(Client client) {
        return toRegisteredClient(client, UUID.randomUUID().toString());
    }

    public RegisteredClient toRegisteredClient(Client client, String id) {
        var reg = client.getRegistration();
        return RegisteredClient.withId(id)
                .clientId(reg.getClientId())
                .clientSecret(reg.getClientSecret())
                .clientName(reg.getClientName())
                .clientAuthenticationMethods(m -> reg.getClientAuthenticationMethods()
                        .stream().map(ClientAuthenticationMethod::new).forEach(m::add))
                .authorizationGrantTypes(t -> reg.getAuthorizationGrantTypes()
                        .stream().map(AuthorizationGrantType::new).forEach(t::add))
                .redirectUris(u -> u.addAll(reg.getRedirectUris()))
                .postLogoutRedirectUris(u -> u.addAll(reg.getPostLogoutRedirectUris()))
                .scopes(s -> s.addAll(reg.getScopes()))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(client.isRequireAuthorizationConsent())
                        .requireProofKey(client.isRequireProofKey())
                        .build())
                .build();
    }

    private Set<String> parseSet(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}