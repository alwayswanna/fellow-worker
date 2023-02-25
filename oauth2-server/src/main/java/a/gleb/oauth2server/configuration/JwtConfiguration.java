/*
 * Copyright (c) 07-3/8/23, 10:11 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration;

import a.gleb.oauth2server.service.TokenService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class JwtConfiguration {

    private static final String ROLE_CLAIM = "role";

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        var keyPair = generateRsaKey();
        var publicKey = (RSAPublicKey) keyPair.getPublic();
        var privateKey = keyPair.getPrivate();
        var rsaKey =
                new RSAKey
                        .Builder(publicKey)
                        .privateKey(privateKey)
                        .keyID(UUID.randomUUID().toString())
                        .build();
        var jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(TokenService tokenService) {
        return context -> {
            var principal = context.getPrincipal();
            var userRoles = principal.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            /* uses here set for does not create mixin-class */
            context.getClaims().claim(ROLE_CLAIM, userRoles);

            tokenService.enrichJwtToken(principal.getName())
                    .forEach((key, value) -> context.getClaims().claim(key, value));

        };
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair = null;
        try {
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return keyPair;
    }
}
