package a.gleb.user_app.config;

import a.gleb.user_app.auth.UserServiceAuthToken;
import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.db.repository.UserEntityRepository;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static a.gleb.user_app.constant.UserAppConstant.LOCK_TABLE;

@OpenAPIDefinition(
        info = @Info(
                title = "user-app",
                description = "User Management API, OAuth2 authorization server.",
                version = "1"
        )
)
@SecurityScheme(
        name = UserAppConfiguration.OAUTH_SECURITY_SCHEME,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "/oauth2/authorize",
                        tokenUrl = "/oauth2/token"
                )
        )
)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(UserAppConfigurationProperties.class)
public class UserAppConfiguration {

    public static final String OAUTH_SECURITY_SCHEME = "authorizationServerSecurityScheme";

    private final UserAppConfigurationProperties properties;
    private final UserEntityRepository userEntityRepository;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity httpSecurity
    ) {
        var requestCache = new HttpSessionRequestCache();
        requestCache.setRequestMatcher(request -> request.getRequestURI().startsWith("/oauth2/authorize"));

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(cache -> cache.requestCache(requestCache))
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(it -> {
                    it.requestMatchers("/login", "/login?error", "/login?logout").permitAll();
                    it.requestMatchers(properties.unprotectedPatterns().toArray(String[]::new)).permitAll();
                    if (!CollectionUtils.isEmpty(properties.securityConstraints())) {
                        properties.securityConstraints().forEach(securityConstraint -> {
                            var authRoles = securityConstraint.roles();
                            securityConstraint.securityCollections().forEach(securityCollection -> {
                                var patterns = securityCollection.patterns();
                                if (securityCollection.methods() == null || securityCollection.methods().isEmpty()) {
                                    it.requestMatchers(patterns.toArray(String[]::new)).hasAnyRole(authRoles.toArray(String[]::new));
                                    return;
                                }
                                securityCollection.methods().forEach(method ->
                                        it.requestMatchers(HttpMethod.valueOf(method), patterns.toArray(String[]::new)).hasAnyRole(authRoles.toArray(String[]::new))
                                );
                            });
                        });
                    }
                    it.anyRequest().authenticated();
                })
                .formLogin(form -> form.loginPage("/login").permitAll())
                .oauth2ResourceServer(it -> it.jwt(converter -> converter.jwtAuthenticationConverter(this::jwtAuthConverter)));

        return httpSecurity.build();
    }

    @Bean
    public MinioClient minioClient() {
        var minio = properties.minio();
        return MinioClient.builder()
                .endpoint(minio.endpoint())
                .credentials(minio.accessKey(), minio.secretKey())
                .build();
    }

    private AbstractAuthenticationToken jwtAuthConverter(Jwt jwt) {
        var subject = jwt.getSubject();
        var authorities = userEntityRepository.findRoleCodeByLogin(subject)
                .map(projection -> {
                    List<GrantedAuthority> list = new ArrayList<>();
                    list.add(new SimpleGrantedAuthority("ROLE_AUTHORIZED"));
                    list.add(new SimpleGrantedAuthority("ROLE_%s".formatted(projection.getRoleCode())));
                    return list;
                })
                .orElseGet(() -> List.of(new SimpleGrantedAuthority("AUTHORIZED")));
        var token = new UserServiceAuthToken(authorities);
        token.setLogin(subject);
        token.setJwt(jwt);
        return token;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .withTableName(LOCK_TABLE)
                        .usingDbTime()
                        .build()
        );
    }
}
