/*
 * Copyright (c) 07-3/30/23, 10:34 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.user_service.configuration;

import a.gleb.user_service.configuration.properties.OAuth2ServerConfigurationProperties;
import a.gleb.user_service.db.repository.authorization.AuthorizationClientRepository;
import a.gleb.user_service.db.repository.authorization.AuthorizationRepository;
import a.gleb.user_service.mapper.authorization.AuthorizationClientMapper;
import a.gleb.user_service.mapper.authorization.AuthorizationMapper;
import a.gleb.user_service.service.authorization.dao.JpaOAuth2AuthorizationService;
import a.gleb.user_service.service.authorization.dao.JpaRegisteredClientRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer.authorizationServer;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableConfigurationProperties(OAuth2ServerConfigurationProperties.class)
public class OAuth2ServerConfiguration {

    private final OAuth2ServerConfigurationProperties properties;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = authorizationServer();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(it -> {
                    var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.cors());
                    it.configurationSource(urlBasedCorsConfigurationSource);
                })
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())
                )
                .authorizeHttpRequests(
                        authorize -> authorize.anyRequest().authenticated()
                )
                .exceptionHandling(exception ->
                        exception.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        ));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(properties.encoderStrength());
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public JpaRegisteredClientRepository registeredClientRepository(
            AuthorizationClientMapper authorizationClientMapper,
            AuthorizationClientRepository authorizationClientRepository
    ) {
        return new JpaRegisteredClientRepository(authorizationClientMapper, authorizationClientRepository);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
            AuthorizationMapper authorizationMapper,
            AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository
    ) {
        return new JpaOAuth2AuthorizationService(authorizationMapper, authorizationRepository, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
