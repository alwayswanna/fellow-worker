/*
 * Copyright (c) 07-2/5/23, 11:50 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.user_service.configuration;

import a.gleb.user_service.configuration.properties.OAuth2ServerConfigurationProperties;
import a.gleb.user_service.db.repository.AccountRepository;
import a.gleb.user_service.db.repository.authorization.AuthorizationClientRepository;
import a.gleb.user_service.service.authorization.customizer.OidcAndAccessTokenClaimsCustomizer;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
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

import javax.sql.DataSource;

@Configuration
@EnableScheduling
@AllArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "${user-service.default-lock-at-most-for}")
public class UserServiceConfiguration {

    private final OAuth2ServerConfigurationProperties properties;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(it -> {
                    var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
                    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", properties.cors());
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
     *      * Configure authorize HTTP request ([protect | unprotect] patterns).
     *      *
     *      * @param authorize matcher registry for configure HTTP patterns.
     *      */
    private void configureAuthorizeHttpRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize
    ) {
        /* configure unprotected patterns */
        var unprotectedPatterns = properties.unprotectedPatterns().toArray(String[]::new);
        authorize.requestMatchers(unprotectedPatterns).permitAll();

        /* protect methods & patterns */
        var securityConstraints = properties.securityConstraints();
        if (!CollectionUtils.isEmpty(securityConstraints)) {
            securityConstraints.forEach(sc -> {
                for (var secColl : sc.securityCollections()) {
                    var roles = sc.roles().toArray(String[]::new);

                    if (!CollectionUtils.isEmpty(secColl.methods())) {
                        var patterns = secColl.patterns().toArray(String[]::new);
                        secColl.patterns().forEach(method ->
                                authorize.requestMatchers(HttpMethod.valueOf(method), patterns).hasAnyRole(roles)
                        );
                    } else {
                        var patterns = secColl.patterns().toArray(String[]::new);
                        authorize.requestMatchers(patterns).hasAnyRole(roles);
                    }
                }
            });
        }

        /* any request authenticated */
        authorize.anyRequest().authenticated();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()
                        .build()
        );
    }
}
