package a.gleb.oauth2server.mapper.authorization;

import a.gleb.oauth2server.db.entity.authorization.AuthorizationEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.COMMA;
import static org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER;

@Component
public class AuthorizationMapper extends OAuth2Mapper{
    
    public AuthorizationMapper(ObjectMapper objectMapper) {
        super(objectMapper);
    }
    
    public AuthorizationEntity toEntity(OAuth2Authorization authorization) {
        var builder = AuthorizationEntity.builder()
                .id(authorization.getId() == null ? null : UUID.fromString(authorization.getId()))
                .registeredClientId(authorization.getRegisteredClientId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(authorization.getAuthorizationGrantType().getValue())
                .authorizedScopes(String.join(COMMA, authorization.getAuthorizedScopes()))
                .attributes(writeMap(authorization.getAttributes()))
                .state(authorization.getAttribute(OAuth2ParameterNames.STATE))
                .dateCreate(LocalDateTime.now());

        var authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        setToken(
                authorizationCode,
                builder::authorizationCodeValue,
                builder::authorizationCodeIssuedAt,
                builder::authorizationCodeExpiresAt,
                builder::authorizationCodeMetadata
        );

        var accessToken = authorization.getToken(OAuth2AccessToken.class);
        setToken(
                accessToken,
                builder::accessTokenValue,
                builder::accessTokenIssuedAt,
                builder::authorizationCodeExpiresAt,
                builder::accessTokenMetadata
        );
        if (accessToken != null) {
            Optional.ofNullable(accessToken.getToken())
                    .map(OAuth2AccessToken::getTokenType)
                    .map(OAuth2AccessToken.TokenType::getValue)
                    .ifPresent(builder::accessTokenType);
        }
        if (accessToken != null) {
            Optional.ofNullable(accessToken.getToken())
                    .map(OAuth2AccessToken::getScopes)
                    .ifPresent(it -> {
                        var scopes = String.join(COMMA, it);
                        builder.accessTokenScopes(scopes);
                    });
        }


        var refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        setToken(
                refreshToken,
                builder::refreshTokenValue,
                builder::refreshTokenIssuedAt,
                builder::refreshTokenExpiresAt,
                builder::refreshTokenMetadata
        );

        var oidcToken = authorization.getToken(OidcIdToken.class);
        setToken(
                oidcToken,
                builder::oidcIdTokenValue,
                builder::oidcIdTokenIssuedAt,
                builder::oidcIdTokenExpiresAt,
                builder::oidcIdTokenMetadata
        );

        if (oidcToken != null) {
            builder.oidcIdTokenClaims(writeMap(oidcToken.getClaims()));
        }

        var deviceCodeToken = authorization.getToken(OAuth2DeviceCode.class);
        setToken(
                deviceCodeToken,
                builder::deviceCodeValue,
                builder::deviceCodeIssuedAt,
                builder::deviceCodeExpiresAt,
                builder::deviceCodeMetadata
        );

        return builder.build();
    }

    public OAuth2Authorization toOAuth2Authorization(AuthorizationEntity authorizationEntity, RegisteredClient registeredClient) {
        if (registeredClient == null) {
            throw new IllegalArgumentException("Attempt mapping authorization entity, with nullable registered client");
        }

        var builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(authorizationEntity.getId().toString())
                .principalName(authorizationEntity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(authorizationEntity.getAuthorizationGrantType()))
                .authorizedScopes(Arrays.stream(authorizationEntity.getAuthorizedScopes().split(COMMA)).collect(Collectors.toSet()))
                .attributes(it -> it.putAll(parseMap(authorizationEntity.getAttributes())));

        if (StringUtils.isNotEmpty(authorizationEntity.getAuthorizationCodeValue())) {
            var authorizationCode = new OAuth2AuthorizationCode(
                    authorizationEntity.getAuthorizationCodeValue(),
                    authorizationEntity.getAuthorizationCodeIssuedAt(),
                    authorizationEntity.getAuthorizationCodeExpiresAt()
            );

            applyTokenValue(builder, authorizationCode, authorizationEntity.getAuthorizationCodeMetadata());
        }

        if (StringUtils.isNotEmpty(authorizationEntity.getAccessTokenValue())) {
            var accessToken = new OAuth2AccessToken(
                    BEARER,
                    authorizationEntity.getAccessTokenValue(),
                    authorizationEntity.getAccessTokenIssuedAt(),
                    authorizationEntity.getAccessTokenExpiresAt(),
                    Arrays.stream(authorizationEntity.getAccessTokenScopes().split(COMMA)).collect(Collectors.toSet())
            );

            applyTokenValue(builder, accessToken, authorizationEntity.getAccessTokenMetadata());
        }

        if (StringUtils.isNotEmpty(authorizationEntity.getRefreshTokenValue())) {
            var refreshToken = new OAuth2RefreshToken(
                    authorizationEntity.getRefreshTokenValue(),
                    authorizationEntity.getRefreshTokenIssuedAt(),
                    authorizationEntity.getRefreshTokenExpiresAt()
            );

            applyTokenValue(builder, refreshToken, authorizationEntity.getRefreshTokenMetadata());
        }

        if (StringUtils.isNotEmpty(authorizationEntity.getOidcIdTokenValue())) {
            var oidcToken = new OidcIdToken(
                    authorizationEntity.getOidcIdTokenValue(),
                    authorizationEntity.getOidcIdTokenIssuedAt(),
                    authorizationEntity.getOidcIdTokenExpiresAt(),
                    parseMap(authorizationEntity.getOidcIdTokenClaims())
            );

            applyTokenValue(builder, oidcToken, authorizationEntity.getOidcIdTokenMetadata());
        }

        if (StringUtils.isNotEmpty(authorizationEntity.getUserCodeValue())) {
            var userCode = new OAuth2UserCode(
                    authorizationEntity.getUserCodeValue(),
                    authorizationEntity.getUserCodeIssuedAt(),
                    authorizationEntity.getUserCodeExpiresAt()
            );

            applyTokenValue(builder, userCode, authorizationEntity.getUserCodeMetadata());
        }

        if (StringUtils.isNotEmpty(authorizationEntity.getDeviceCodeValue())) {
            var deviceCode = new OAuth2DeviceCode(
                    authorizationEntity.getDeviceCodeValue(),
                    authorizationEntity.getDeviceCodeIssuedAt(),
                    authorizationEntity.getDeviceCodeExpiresAt()
            );

            applyTokenValue(builder, deviceCode, authorizationEntity.getDeviceCodeMetadata());
        }

        return builder.build();
    }

    private void setToken(
            Token<?> token,
            Consumer<String> value,
            Consumer<Instant> issuedAt,
            Consumer<Instant> expiresAt,
            Consumer<String> metadata
    ) {
        if (token != null) {
            var tokenValue = token.getToken();
            value.accept(tokenValue.getTokenValue());
            issuedAt.accept(tokenValue.getIssuedAt());
            expiresAt.accept(tokenValue.getExpiresAt());
            metadata.accept(writeMap(token.getMetadata()));
        }
    }

    private void applyTokenValue(OAuth2Authorization.Builder builder, AbstractOAuth2Token token, String metadata) {
        builder.token(token, it -> it.putAll(parseMap(metadata)));
    }
}
