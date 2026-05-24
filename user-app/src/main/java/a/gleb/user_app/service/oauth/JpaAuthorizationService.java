package a.gleb.user_app.service.oauth;

import a.gleb.user_app.db.repository.oauth.AuthorizationEntityRepository;
import a.gleb.user_app.mapper.oauth.AuthorizationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaAuthorizationService implements OAuth2AuthorizationService {

    private final AuthorizationMapper authorizationMapper;
    private final AuthorizationEntityRepository authorizationEntityRepository;

    @Override
    public void save(OAuth2Authorization authorization) {
        var entity = authorizationMapper.toEntity(authorization);
        authorizationEntityRepository.save(entity);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        authorizationEntityRepository.deleteById(UUID.fromString(authorization.getId()));
    }

    @Override
    public @Nullable OAuth2Authorization findById(String id) {
        return authorizationEntityRepository.findById(UUID.fromString(id))
                .map(authorizationMapper::toOAuth2Authorization)
                .orElse(null);
    }

    @Override
    public @Nullable OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null || StringUtils.isEmpty(token)) {
            log.error("JpaAuthorizationService: attempt to find Authorization with nullable or empty TOKEN.");
            return null;
        }

        var value = tokenType.getValue();
        var authorization = switch (value) {
            case OAuth2ParameterNames.STATE -> authorizationEntityRepository.findByState(token);
            case OAuth2ParameterNames.ACCESS_TOKEN -> authorizationEntityRepository.findByAccessTokenValue(token);
            case OAuth2ParameterNames.CODE -> authorizationEntityRepository.findByAuthorizationCodeValue(token);
            case OAuth2ParameterNames.REFRESH_TOKEN -> authorizationEntityRepository.findByRefreshTokenValue(token);
            case OidcParameterNames.ID_TOKEN -> authorizationEntityRepository.findByOidcIdTokenValue(token);
            case OAuth2ParameterNames.USER_CODE -> authorizationEntityRepository.findByUserCodeValue(token);
            case OAuth2ParameterNames.DEVICE_CODE -> authorizationEntityRepository.findByDeviceCodeValue(token);
            default -> authorizationEntityRepository.findByDifferenceTokenValue(token);
        };

        return authorization
                .map(authorizationMapper::toOAuth2Authorization)
                .orElse(null);
    }
}
