package a.gleb.user_app.service.oauth;

import a.gleb.user_app.db.repository.oauth.ClientEntityRepository;
import a.gleb.user_app.mapper.oauth.RegisteredClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaRegisteredClientService implements RegisteredClientRepository {

    private final RegisteredClientMapper registeredClientMapper;
    private final ClientEntityRepository clientEntityRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        var entity = registeredClientMapper.toEntity(registeredClient);
        clientEntityRepository.save(entity);
    }

    @Override
    public @Nullable RegisteredClient findById(String id) {
        return clientEntityRepository.findById(UUID.fromString(id))
                .map(registeredClientMapper::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public @Nullable RegisteredClient findByClientId(String clientId) {
        return clientEntityRepository.findClientEntityByClientId(clientId)
                .map(registeredClientMapper::toRegisteredClient)
                .orElse(null);
    }

    public boolean existsByClientId(String clientId) {
        return clientEntityRepository.existsClientEntityByClientId(clientId);
    }
}
