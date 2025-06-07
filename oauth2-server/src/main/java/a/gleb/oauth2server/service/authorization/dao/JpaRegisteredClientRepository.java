package a.gleb.oauth2server.service.authorization.dao;

import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.db.entity.authorization.AuthorizationClientEntity;
import a.gleb.oauth2server.db.repository.authorization.AuthorizationClientRepository;
import a.gleb.oauth2server.mapper.authorization.AuthorizationClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final AuthorizationClientMapper authorizationClientMapper;
    private final AuthorizationClientRepository authorizationClientRepository;

    /**
     * Save or update {@link RegisteredClient} to database.
     *
     * @param registeredClient entity to save.
     */
    @Override
    public void save(RegisteredClient registeredClient) {
        var authorizationClientEntity = validateAndMapClient(registeredClient);

        /* set ID if client, with client_id already exists. */
        authorizationClientRepository
                .findAuthorizationClientEntityByClientId(registeredClient.getClientId())
                .ifPresent(it -> authorizationClientEntity.setId(it.getId()));

        authorizationClientRepository.save(authorizationClientEntity);
    }

    public void saveWithRoles(RegisteredClient registeredClient, Set<RoleEntity> entities) {
        var authorizationClientEntity = validateAndMapClient(registeredClient);
        authorizationClientEntity.setRoles(entities);

        /* set ID if client, with client_id already exists. */
        authorizationClientRepository
                .findAuthorizationClientEntityByClientId(registeredClient.getClientId())
                .ifPresent(it -> authorizationClientEntity.setId(it.getId()));

        authorizationClientRepository.save(authorizationClientEntity);
    }

    /**
     * Search {@link RegisteredClient} by ID.
     *
     * @param id identifier of client.
     * @return {@link RegisteredClient} client for authorization.
     */
    @Override
    public RegisteredClient findById(String id) {
        if (id == null) {
            log.debug("Can not find registered client, [id={}]", id);
            return null;
        }

        var registeredClientId = UUID.fromString(id);
        var authorizationClientEntity = authorizationClientRepository.findById(registeredClientId);

        var registeredClient = authorizationClientEntity
                .map(authorizationClientMapper::toRegisteredClient)
                .orElse(null);

        if (registeredClient != null) {
            log.debug("Found RegisteredClient by Id, [client_id={}]", registeredClient.getClientId());
        }

        return registeredClient;
    }

    /**
     * Search {@link RegisteredClient} by ClientId.
     *
     * @param clientId of registered client.
     * @return {@link RegisteredClient} client for authorization.
     */
    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = authorizationClientRepository.findAuthorizationClientEntityByClientId(clientId)
                .map(authorizationClientMapper::toRegisteredClient)
                .orElse(null);

        if (client != null) {
            log.debug("Found RegisteredClient by clientId, [client_id={}]", client.getClientId());
        }

        return client;
    }

    /**
     * Is exist client with clientId.
     *
     * @param clientId of registered client.
     * @return {@link Boolean} true if client with clientId exist in database.
     */
    public boolean existByClientId(String clientId) {
        return authorizationClientRepository.existsByClientId(clientId);
    }

    private AuthorizationClientEntity validateAndMapClient(RegisteredClient registeredClient) {
        if (registeredClient == null) {
            throw new IllegalArgumentException("Can not save or update RegisteredClient, client in null");
        }

        log.debug("Save RegisteredClient, [client_id={}]", registeredClient.getClientId());
        return authorizationClientMapper.toEntity(registeredClient);
    }
}
