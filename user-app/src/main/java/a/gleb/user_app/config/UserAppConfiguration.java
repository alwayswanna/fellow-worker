package a.gleb.user_app.config;

import a.gleb.user_app.adapter.in.security.auth.UserServiceAuthToken;
import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
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
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
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
import org.springframework.retry.annotation.EnableRetry;

import javax.sql.DataSource;
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
@EnableRetry
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(UserAppConfigurationProperties.class)
public class UserAppConfiguration {

    public static final String OAUTH_SECURITY_SCHEME = "authorizationServerSecurityScheme";

    private final UserAppConfigurationProperties properties;

    /**
     * PUBLIC/AUTHENTICATED/роль для REST API (/api/v1/**) теперь решает gateway
     * ({@code AuthorizationFilter}, правила в {@code app.authorization}, включая
     * {@code ADMIN → /api/v1/admin/**}, перенесённое отсюда из {@code app.security-constraints}) —
     * здесь остаётся только JWT-парсинг (нужен {@code /me}-эндпоинтам) и сам интерактивный OAuth2
     * AS flow (форма логина, сессия) — он сессионный, не Bearer, и вообще не проходит через
     * gateway-авторизацию (см. {@code /user-app/oauth2/**}/{@code /login} в правилах gateway).
     */
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
                .authorizeHttpRequests(it -> it.anyRequest().permitAll())
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

    /**
     * Role comes straight from the JWT's {@code role} claim (minted by
     * {@link OAuth2ServerConfiguration#tokenCustomizer}), not a per-request DB lookup — this is
     * the authorization server's own resource-server filter, so every token it issues to itself
     * already carries the role. A role change takes effect on the user's next token refresh
     * (access tokens are short-lived — see {@code registered-client-default-options}), not
     * instantly; that trade-off replaces a DB round trip on every authenticated request here with
     * a bounded staleness window instead.
     */
    private AbstractAuthenticationToken jwtAuthConverter(Jwt jwt) {
        var subject = jwt.getSubject();
        var roleCode = jwt.getClaimAsString("role");
        List<GrantedAuthority> authorities = roleCode != null
                ? List.of(new SimpleGrantedAuthority("ROLE_AUTHORIZED"), new SimpleGrantedAuthority("ROLE_%s".formatted(roleCode)))
                : List.of(new SimpleGrantedAuthority("AUTHORIZED"));
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
