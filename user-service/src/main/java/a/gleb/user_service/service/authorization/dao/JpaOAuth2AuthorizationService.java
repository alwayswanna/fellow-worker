package a.gleb.user_service.service.authorization.dao;

import a.gleb.user_service.db.repository.authorization.AuthorizationRepository;
import a.gleb.user_service.mapper.authorization.AuthorizationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.UUID;

@Slf4j
public record JpaOAuth2AuthorizationService(
        AuthorizationMapper authorizationMapper,
        AuthorizationRepository authorizationRepository,
        RegisteredClientRepository registeredClientRepository
) implements OAuth2AuthorizationService {

    /**
     * Save or update {@link OAuth2Authorization} to database.
     *
     * @param authorization entity to save or update.
     */
    @Override
    public void save(OAuth2Authorization authorization) {
        if (authorization == null) {
            throw new IllegalArgumentException("Can not save or update OAuth2Authorization, object is null");
        }
        log.debug("Save or update authorization, [id={}]", authorization.getId());
        var authorizationEntity = authorizationMapper.toEntity(authorization);

        authorizationRepository.findById(UUID.fromString(authorization.getId()))
                .ifPresent(it -> authorizationEntity.setId(it.getId()));

        authorizationRepository.save(authorizationEntity);
    }

    /**
     * Remove authorization by ID.
     *
     * @param authorization entity to remove.
     */
    @Override
    public void remove(OAuth2Authorization authorization) {
        if (authorization == null) {
            return;
        }

        log.debug("Remove authorization, [id={}]", authorization.getId());
        var authorizationId = UUID.fromString(authorization.getId());

        authorizationRepository.removeById(authorizationId);
    }

    /**
     * Search {@link OAuth2Authorization} by ID.
     *
     * @param id identifier of authorization.
     * @return {@link OAuth2Authorization} authorization.
     */
    @Override
    public OAuth2Authorization findById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        var oAuth2AuthorizationId = UUID.fromString(id);
        var entity = authorizationRepository.findById(oAuth2AuthorizationId);

        if (entity.isPresent()) {
            var authorizationEntity = entity.get();
            var registeredClient = registeredClientRepository.findByClientId(
                    authorizationEntity.getRegisteredClientId()
            );

            log.debug("Found OAuth2Authorization by Id, [id={}]", id);

            return authorizationMapper.toOAuth2Authorization(authorizationEntity, registeredClient);
        } else {
            return null;
        }
    }

    /**
     * Search {@link OAuth2Authorization} by ClientId.
     *
     * @param token     value
     * @param tokenType type of token
     * @return {@link OAuth2Authorization} client for authorization.
     */
    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        if (StringUtils.isEmpty(token) || tokenType == null) {
            return null;
        }

        var value = tokenType.getValue();
        var authorization = switch (value) {
            case OAuth2ParameterNames.STATE -> authorizationRepository.findByState(token);
            case OAuth2ParameterNames.ACCESS_TOKEN -> authorizationRepository.findByAccessTokenValue(token);
            case OAuth2ParameterNames.CODE -> authorizationRepository.findByAuthorizationCodeValue(token);
            case OAuth2ParameterNames.REFRESH_TOKEN -> authorizationRepository.findByRefreshTokenValue(token);
            case OidcParameterNames.ID_TOKEN -> authorizationRepository.findByOidcIdTokenValue(token);
            case OAuth2ParameterNames.USER_CODE -> authorizationRepository.findByUserCodeValue(token);
            case OAuth2ParameterNames.DEVICE_CODE -> authorizationRepository.findByDeviceCodeValue(token);
            default -> authorizationRepository.findByDifferenceTokenValue(token);
        };

        if (authorization.isPresent()) {
            var oAuth2Authorization = authorization.get();
            var registeredClient = registeredClientRepository.findById(
                    oAuth2Authorization.getRegisteredClientId()
            );

            log.debug("Found OAuth2Authorization by type: {}, [id={}]", value, oAuth2Authorization.getId());

            return authorizationMapper.toOAuth2Authorization(oAuth2Authorization, registeredClient);
        } else {
            return null;
        }
    }
}
