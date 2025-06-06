/*
 * Copyright (c) 07-3/30/23, 10:34 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration;

import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.UUID;

import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.NONE;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.PRIVATE_KEY_JWT;
import static org.springframework.security.oauth2.core.oidc.OidcScopes.OPENID;
import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EntityScan(basePackageClasses = {
        Account.class
})
@EnableJpaRepositories(basePackageClasses = {
        AccountRepository.class
})
@EnableConfigurationProperties(OAuth2ServerProperties.class)
public class OAuth2ServerConfiguration {

    private final OAuth2ServerProperties properties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(properties.getEncoderStrength());
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = authorizationServer();

        http
                .cors(it -> {
                    var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.getCors());
                    it.configurationSource(urlBasedCorsConfigurationSource);
                })
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> {
                    authorize.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> {
                    exception.defaultAuthenticationEntryPointFor(
                            new LoginUrlAuthenticationEntryPoint("/login"),
                            new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    );
                });

        return http.build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
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
