/*
 * Copyright (c) 12-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.configuration;

import a.gleb.clientmanager.configuration.properties.ClientManagerConfigurationProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ClientManagerSecurityConfiguration {

    private static final List<String> DEFAULT_UNPROTECTED_PATTERNS = new ArrayList<>(Arrays.asList(
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/error"
    ));

    private final ClientManagerConfigurationProperties properties;

    @Bean
    public SecurityFilterChain oauth2SecurityWebFilterChain(
            HttpSecurity httpSecurity
    ) throws Exception {
        httpSecurity
                .anonymous().disable()
                .cors().disable()
                .csrf().disable()
                .authorizeHttpRequests(this::configure)
                .oauth2ResourceServer()
                .jwt(jwtConfigurer -> {
                    var converter = new JwtAuthenticationConverter();
                    converter.setJwtGrantedAuthoritiesConverter(new SpringBootOAuth2Converter());
                    jwtConfigurer.jwtAuthenticationConverter(converter);
                });

        return httpSecurity.build();
    }

    private void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
    ) {
        properties.getSecurityConstraints().forEach(securityConstraints ->
                {
                    var roles = securityConstraints.getRoles();
                    securityConstraints.getSecurityCollections().forEach(securityCollection -> {
                        var patterns = securityCollection.getPatterns();
                        if (securityCollection.getMethods() != null && !securityCollection.getMethods().isEmpty()) {
                            securityCollection.getMethods().forEach(method -> {
                                auth.requestMatchers(HttpMethod.valueOf(method), patterns.toArray(String[]::new))
                                        .hasAnyRole(roles.toArray(String[]::new));
                            });
                        } else {
                            auth.requestMatchers(patterns.toArray(String[]::new))
                                    .hasAnyRole(roles.toArray(String[]::new));
                        }
                    });
                }
        );

        DEFAULT_UNPROTECTED_PATTERNS.addAll(properties.getUnprotectedPatterns());
        auth.requestMatchers(DEFAULT_UNPROTECTED_PATTERNS.toArray(String[]::new)).permitAll();
    }

    private static class SpringBootOAuth2Converter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private static final String ROLE_PREFIX = "ROLE_%s";
        private static final String ROLE_KEY_CLAIM = "role";

        @Override
        public List<GrantedAuthority> convert(Jwt source) {

            return Arrays.stream(
                            source.getClaims().get(ROLE_KEY_CLAIM)
                                    .toString()
                                    .replace("\\[", "")
                                    .replace("\\]", "")
                                    .split(","))
                    .map(rolesWithSymbols -> rolesWithSymbols.replaceAll("\\W", ""))
                    .map(role -> String.format(ROLE_PREFIX, role))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }
}
