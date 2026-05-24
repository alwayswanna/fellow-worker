package a.gleb.user_app.mapper.oauth;

import a.gleb.user_app.db.entity.oauth.AuthorizationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthorizationMapper extends AbstractOAuthMapper {

    private final RegisteredClientRepository registeredClientRepository;

    public AuthorizationEntity toEntity(OAuth2Authorization authorization) {
        var builder = AuthorizationEntity.builder()
                .id(UUID.fromString(authorization.getId()))
                .registeredClientId(authorization.getRegisteredClientId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(authorization.getAuthorizationGrantType().getValue())
                .authorizedScopes(String.join(",", authorization.getAuthorizedScopes()))
                .attributes(writeMap(authorization.getAttributes()))
                .state(authorization.getAttribute(OAuth2ParameterNames.STATE));

        var authCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (authCode != null) {
            builder.authorizationCodeValue(authCode.getToken().getTokenValue())
                    .authorizationCodeIssuedAt(authCode.getToken().getIssuedAt())
                    .authorizationCodeExpiresAt(authCode.getToken().getExpiresAt())
                    .authorizationCodeMetadata(writeMap(authCode.getMetadata()));
        }

        var accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            builder.accessTokenValue(accessToken.getToken().getTokenValue())
                    .accessTokenIssuedAt(accessToken.getToken().getIssuedAt())
                    .accessTokenExpiresAt(accessToken.getToken().getExpiresAt())
                    .accessTokenMetadata(writeMap(accessToken.getMetadata()))
                    .accessTokenType(accessToken.getToken().getTokenType().getValue())
                    .accessTokenScopes(String.join(",", accessToken.getToken().getScopes()));
        }

        var refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            builder.refreshTokenValue(refreshToken.getToken().getTokenValue())
                    .refreshTokenIssuedAt(refreshToken.getToken().getIssuedAt())
                    .refreshTokenExpiresAt(refreshToken.getToken().getExpiresAt())
                    .refreshTokenMetadata(writeMap(refreshToken.getMetadata()));
        }

        var oidcToken = authorization.getToken(OidcIdToken.class);
        if (oidcToken != null) {
            builder.oidcIdTokenValue(oidcToken.getToken().getTokenValue())
                    .oidcIdTokenIssuedAt(oidcToken.getToken().getIssuedAt())
                    .oidcIdTokenExpiresAt(oidcToken.getToken().getExpiresAt())
                    .oidcIdTokenMetadata(writeMap(oidcToken.getMetadata()))
                    .oidcIdTokenClaims(writeMap(oidcToken.getToken().getClaims()));
        }

        var userCode = authorization.getToken(OAuth2UserCode.class);
        if (userCode != null) {
            builder.userCodeValue(userCode.getToken().getTokenValue())
                    .userCodeIssuedAt(userCode.getToken().getIssuedAt())
                    .userCodeExpiresAt(userCode.getToken().getExpiresAt())
                    .userCodeMetadata(writeMap(userCode.getMetadata()));
        }

        var deviceCode = authorization.getToken(OAuth2DeviceCode.class);
        if (deviceCode != null) {
            builder.deviceCodeValue(deviceCode.getToken().getTokenValue())
                    .deviceCodeIssuedAt(deviceCode.getToken().getIssuedAt())
                    .deviceCodeExpiresAt(deviceCode.getToken().getExpiresAt())
                    .deviceCodeMetadata(writeMap(deviceCode.getMetadata()));
        }

        return builder.build();
    }

    public OAuth2Authorization toOAuth2Authorization(AuthorizationEntity entity) {
        var registeredClient = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new IllegalStateException("RegisteredClient not found: " + entity.getRegisteredClientId());
        }

        var builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId().toString())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(parseSet(entity.getAuthorizedScopes()))
                .attributes(attrs -> attrs.putAll(readMap(entity.getAttributes())));

        if (entity.getAuthorizationCodeValue() != null) {
            var token = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    entity.getAuthorizationCodeIssuedAt(),
                    entity.getAuthorizationCodeExpiresAt()
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            var token = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(),
                    parseSet(entity.getAccessTokenScopes())
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            var token = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(),
                    entity.getRefreshTokenExpiresAt()
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getRefreshTokenMetadata())));
        }

        if (entity.getOidcIdTokenValue() != null) {
            var token = new OidcIdToken(
                    entity.getOidcIdTokenValue(),
                    entity.getOidcIdTokenIssuedAt(),
                    entity.getOidcIdTokenExpiresAt(),
                    readMap(entity.getOidcIdTokenClaims())
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getOidcIdTokenMetadata())));
        }

        if (entity.getUserCodeValue() != null) {
            var token = new OAuth2UserCode(
                    entity.getUserCodeValue(),
                    entity.getUserCodeIssuedAt(),
                    entity.getUserCodeExpiresAt()
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getUserCodeMetadata())));
        }

        if (entity.getDeviceCodeValue() != null) {
            var token = new OAuth2DeviceCode(
                    entity.getDeviceCodeValue(),
                    entity.getDeviceCodeIssuedAt(),
                    entity.getDeviceCodeExpiresAt()
            );
            builder.token(token, meta -> meta.putAll(readMap(entity.getDeviceCodeMetadata())));
        }

        return builder.build();
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