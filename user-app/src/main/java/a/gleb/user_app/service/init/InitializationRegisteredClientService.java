package a.gleb.user_app.service.init;

import a.gleb.user_app.mapper.oauth.RegisteredClientMapper;
import a.gleb.user_app.service.oauth.JpaRegisteredClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.security.oauth2.server.authorization.autoconfigure.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.init.registered-client", havingValue = "true")
public class InitializationRegisteredClientService implements ApplicationRunner {

    private final RegisteredClientMapper registeredClientMapper;
    private final OAuth2AuthorizationServerProperties properties;
    private final JpaRegisteredClientService jpaRegisteredClientService;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        properties.getClient()
                .values()
                .stream()
                .filter(it -> !jpaRegisteredClientService.existsByClientId(it.getRegistration().getClientId()))
                .forEach(it -> {
                    var registration = it.getRegistration();
                    log.info("InitializationRegisteredClientService, create registered client, [client_id={}]", registration.getClientId());
                    var registeredClient = registeredClientMapper.toRegisteredClient(it);
                    jpaRegisteredClientService.save(registeredClient);
                    log.info("InitializationRegisteredClientService, registered client was created, [client_id={}]", registration.getClientId());
                });
    }
}
