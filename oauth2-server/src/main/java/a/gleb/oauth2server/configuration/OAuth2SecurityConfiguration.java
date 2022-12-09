package a.gleb.oauth2server.configuration;

import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import a.gleb.oauth2server.service.OAuth2UserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AbstractSettings;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.*;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.OPENID;

@Configuration
@AllArgsConstructor
public class OAuth2SecurityConfiguration {

    private final OAuth2ServerProperties properties;

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            OAuth2UserDetailsService oAuth2UserDetailsService
    ) {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(oAuth2UserDetailsService);

        return provider;
    }

    @Bean
    public AbstractSettings clientSettings() {
        return TokenSettings.builder().setting("issuer", properties.getIssueUrl()).build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder
    ) {
        var clients = properties.getDefaultClients().stream()
                .map(clientFromConfig ->
                        RegisteredClient.withId(UUID.randomUUID().toString())
                                .tokenSettings(
                                        TokenSettings.builder()
                                                .accessTokenTimeToLive(
                                                        Duration.ofMinutes(
                                                                clientFromConfig.getDefaultAccessTokenTimeToLive()
                                                        )
                                                )
                                                .refreshTokenTimeToLive(
                                                        Duration.ofDays(
                                                                clientFromConfig.getDefaultRefreshTokenTimeToLive()
                                                        )
                                                )
                                                .build()
                                )
                                .clientId(clientFromConfig.getDefaultClientId())
                                .clientSecret(passwordEncoder.encode(clientFromConfig.getDefaultClientSecret()))
                                .clientAuthenticationMethod(CLIENT_SECRET_BASIC)
                                .clientAuthenticationMethod(CLIENT_SECRET_POST)
                                .clientAuthenticationMethod(CLIENT_SECRET_JWT)
                                .clientAuthenticationMethod(PRIVATE_KEY_JWT)
                                .clientAuthenticationMethod(NONE)
                                .authorizationGrantType(AUTHORIZATION_CODE)
                                .authorizationGrantType(REFRESH_TOKEN)
                                .authorizationGrantType(CLIENT_CREDENTIALS)
                                .redirectUris(redirectConf -> redirectConf.addAll(
                                        clientFromConfig.getDefaultRedirectUris())
                                )
                                .scope(OPENID)
                                .build()
                )
                .toList();

        var registerClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        clients.forEach(client -> {
            if (registerClientRepository.findByClientId(client.getClientId()) == null) {
                registerClientRepository.save(client);
            }
        });

        return registerClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository
    ) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }


}
