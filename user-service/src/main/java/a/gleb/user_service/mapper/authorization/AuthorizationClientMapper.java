package a.gleb.user_service.mapper.authorization;

import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientRequest;
import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientResponse;
import a.gleb.user_service.constant.OAuth2ServerConstants;
import a.gleb.user_service.db.entity.authorization.AuthorizationClientEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import static a.gleb.user_service.constant.OAuth2ServerConstants.COMMA;

@Component
public class AuthorizationClientMapper extends OAuth2Mapper{
    
    private final PasswordEncoder passwordEncoder;

    public AuthorizationClientMapper (PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method for map {@link AuthorizationClientEntity} to response model.
     *
     * @param entity data about existing client.
     * @return {@link AuthorizationClientResponse} existing account data.
     */
    public AuthorizationClientResponse toResponse(AuthorizationClientEntity entity) {
        return new AuthorizationClientResponse(
                entity.getClientId(),
                passwordEncoder.encode(entity.getClientSecret()),
                Arrays.stream(entity.getRedirectUris().split(COMMA)).toList(),
                Arrays.stream(entity.getClientAuthenticationMethods().split(COMMA)).toList(),
                Arrays.stream(entity.getAuthorizationGrantTypes().split(COMMA)).toList(),
                Arrays.stream(entity.getScopes().split(COMMA)).toList(),
                Arrays.stream(entity.getPostLogoutRedirectUris().split(COMMA)).toList()
        );
    }

    public AuthorizationClientEntity toEntity(AuthorizationClientRequest request) {
        return null;
    }

    public AuthorizationClientEntity toEntity(RegisteredClient client) {
        var clientBuilder =  AuthorizationClientEntity.builder()
                .id(client.getId() == null ? null : UUID.fromString(client.getId()))
                .authorizationGrantTypes(
                        String.join(
                                OAuth2ServerConstants.COMMA,
                                client.getAuthorizationGrantTypes().stream().map(AuthorizationGrantType::getValue).toList()
                        )
                )
                .clientAuthenticationMethods(
                        String.join(
                                OAuth2ServerConstants.COMMA,
                                client.getClientAuthenticationMethods().stream().map(ClientAuthenticationMethod::getValue).toList()
                        )
                )
                .scopes(String.join(OAuth2ServerConstants.COMMA, client.getScopes()))
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientName(client.getClientName())
                .redirectUris(String.join(OAuth2ServerConstants.COMMA, client.getRedirectUris()))
                .postLogoutRedirectUris(String.join(OAuth2ServerConstants.COMMA, client.getPostLogoutRedirectUris()))
                .clientSettings(writeMap((client.getClientSettings().getSettings())))
                .tokenSettings(writeMap(client.getTokenSettings().getSettings()));

        if (client.getClientSecretExpiresAt() != null) {
            clientBuilder.clientSecretExpiresAt(
                    LocalDateTime.ofInstant(client.getClientSecretExpiresAt(), ZoneId.systemDefault())
            );
        }

        if (client.getClientIdIssuedAt() != null) {
            clientBuilder.clientIdIssuedAt(
                    LocalDateTime.ofInstant(client.getClientIdIssuedAt(), ZoneId.systemDefault())
            );
        }

               return clientBuilder.build();
    }

    public RegisteredClient toRegisteredClient(AuthorizationClientEntity entity) {
        return RegisteredClient.withId(entity.getId().toString())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt().toInstant(ZoneOffset.UTC))
                .clientSecret(entity.getClientSecret())
                .clientName(entity.getClientName())
                .clientAuthenticationMethods(it ->
                        Arrays.stream(entity.getClientAuthenticationMethods().split(OAuth2ServerConstants.COMMA))
                                .forEach(method -> it.add(new ClientAuthenticationMethod(method)))
                )
                .authorizationGrantTypes(it ->
                        Arrays.stream(entity.getAuthorizationGrantTypes().split(OAuth2ServerConstants.COMMA))
                                .forEach(type -> it.add(new AuthorizationGrantType(type))))
                .redirectUris(it -> it.addAll(Arrays.asList(entity.getRedirectUris().split(OAuth2ServerConstants.COMMA))))
                .postLogoutRedirectUris(it -> it.addAll(Arrays.asList(entity.getPostLogoutRedirectUris().split(OAuth2ServerConstants.COMMA))))
                .scopes(it -> it.addAll(Arrays.asList(entity.getScopes().split(OAuth2ServerConstants.COMMA))))
                .clientSettings(ClientSettings.withSettings(parseMap(entity.getClientSettings())).build())
                .tokenSettings(TokenSettings.withSettings(parseMap(entity.getTokenSettings())).build())
                .build();
    }
}
