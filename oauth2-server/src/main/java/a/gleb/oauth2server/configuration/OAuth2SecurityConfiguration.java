/*
 * Copyright (c) 07-2/5/23, 11:50 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration;

import a.gleb.oauth2server.configuration.properties.OAuth2ServerConfigurationProperties;
import a.gleb.oauth2server.db.repository.AccountRepository;
import a.gleb.oauth2server.db.repository.authorization.AuthorizationClientRepository;
import a.gleb.oauth2server.service.authorization.customizer.OidcAndAccessTokenClaimsCustomizer;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableScheduling
@AllArgsConstructor
public class OAuth2SecurityConfiguration {

    private final OAuth2ServerConfigurationProperties properties;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(it -> {
                    var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.getCors());
                    it.configurationSource(urlBasedCorsConfigurationSource);
                })
                .authorizeHttpRequests(this::configureAuthorizeHttpRequests)
                .formLogin(Customizer.withDefaults())
                .oauth2ResourceServer(it -> it.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer(
            AccountRepository accountRepository,
            AuthorizationClientRepository authorizationClientRepository
    ) {
        return new OidcAndAccessTokenClaimsCustomizer(accountRepository, authorizationClientRepository);
    }

    /**
     * Configure authorize HTTP request ([protect | unprotect] patterns).
     *
     * @param authorize matcher registry for configure HTTP patterns.
     */
    private void configureAuthorizeHttpRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize
    ) {
        /* configure unprotected patterns */
        var unprotectedPatterns = properties.getUnprotectedPatterns().toArray(String[]::new);
        authorize.requestMatchers(unprotectedPatterns).permitAll();

        /* protect methods & patterns */
        var securityConstraints = properties.getSecurityConstraints();
        if (!CollectionUtils.isEmpty(securityConstraints)) {
            securityConstraints.forEach(sc -> {
                for (var secColl : sc.getSecurityCollections()) {
                    var roles = sc.getRoles().toArray(String[]::new);

                    if (!CollectionUtils.isEmpty(secColl.getMethods())) {
                        var patterns = secColl.getPatterns().toArray(String[]::new);
                        secColl.getPatterns().forEach(method ->
                                authorize.requestMatchers(HttpMethod.valueOf(method), patterns).hasAnyRole(roles)
                        );
                    } else {
                        var patterns = secColl.getPatterns().toArray(String[]::new);
                        authorize.requestMatchers(patterns).hasAnyRole(roles);
                    }
                }
            });
        }

        /* any request authenticated */
        authorize.anyRequest().authenticated();
    }
}
