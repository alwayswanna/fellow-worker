package a.gleb.gateway.filter;

import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties;
import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties.Authorization.DefaultAccess;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.PathContainer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Единая точка принятия решения PUBLIC/AUTHENTICATED/роль для всех проксируемых путей — правила
 * приходят из {@code app.authorization} (см. {@code config-server/shared/gateway-app.yaml}).
 *
 * <p>Это единственное место, где валидируется подпись JWT. После успешной проверки claim'ы
 * прокидываются downstream-сервисам как доверенные заголовки {@code X-Account-Id}/{@code X-Role}/
 * {@code X-Login} — сами сервисы JWT больше не декодируют (это безопасно только потому, что они
 * физически недостижимы иначе как через gateway). Поэтому эти три заголовка безусловно вырезаются
 * из входящего запроса ещё до принятия решения PUBLIC/AUTHENTICATED/роль — иначе клиент мог бы
 * подделать identity напрямую, без валидного токена.
 *
 * <p>Порядок выбран так, чтобы фильтр видел оригинальный путь с префиксом сервиса
 * (например {@code /vacancy-app/...}) — то есть раньше route-level {@code stripPrefix} (который
 * получает order ~1 из позиции в {@code .filters(...)}), но внутри обёртки {@link LoggingFilter}
 * (order {@link Ordered#HIGHEST_PRECEDENCE}), чтобы отклонённые запросы тоже попадали в
 * стандартную строку лога "Request completed: ... status=401/403 ...".
 */
@Slf4j
@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private static final String PUBLIC = "PUBLIC";
    private static final String AUTHENTICATED = "AUTHENTICATED";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String ACCOUNT_ID_HEADER = "X-Account-Id";
    private static final String ROLE_HEADER = "X-Role";
    private static final String LOGIN_HEADER = "X-Login";

    private final JsonMapper jsonMapper;
    private final List<CompiledRule> compiledRules;
    private final DefaultAccess defaultAccess;
    private final ReactiveJwtDecoder jwtDecoder;

    public AuthorizationFilter(
            GatewayAppConfigurationProperties properties,
            ReactiveJwtDecoder jwtDecoder,
            JsonMapper jsonMapper
    ) {
        var authorization = properties.getAuthorization();
        var parser = new PathPatternParser();
        this.compiledRules = authorization.rules().stream()
                .map(rule -> new CompiledRule(
                        parser.parse(rule.path()),
                        rule.method() == null ? null : HttpMethod.valueOf(rule.method()),
                        rule.access()))
                .toList();
        this.defaultAccess = authorization.defaultAccess();
        this.jwtDecoder = jwtDecoder;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        // Безусловно, для абсолютно любого запроса — иначе клиент мог бы подделать identity
        // на PUBLIC-пути или до того, как мы успеем декодировать его собственный токен.
        var sanitizedExchange = stripIdentityHeaders(exchange);

        var access = findAccess(sanitizedExchange);
        if (access.contains(PUBLIC)) {
            return chain.filter(sanitizedExchange);
        }

        var token = extractBearerToken(sanitizedExchange);
        if (token == null) {
            return reject(sanitizedExchange, HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }

        return jwtDecoder.decode(token)
                .flatMap(jwt -> {
                    if (!access.contains(AUTHENTICATED)) {
                        var role = jwt.getClaimAsString("role");
                        if (role == null || !access.contains(role)) {
                            return reject(sanitizedExchange, HttpStatus.FORBIDDEN, "Role '%s' is not permitted".formatted(role));
                        }
                    }
                    return chain.filter(withIdentityHeaders(sanitizedExchange, jwt));
                })
                .onErrorResume(JwtException.class, ex -> reject(sanitizedExchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
    }

    private static ServerWebExchange stripIdentityHeaders(ServerWebExchange exchange) {
        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(ACCOUNT_ID_HEADER);
                    headers.remove(ROLE_HEADER);
                    headers.remove(LOGIN_HEADER);
                })
                .build();
        return exchange.mutate().request(mutatedRequest).build();
    }

    // Только отсюда downstream-сервисы узнают account_id/role/login — сами они JWT не декодируют.
    private static ServerWebExchange withIdentityHeaders(ServerWebExchange exchange, Jwt jwt) {
        var accountId = jwt.getClaimAsString("account_id");
        var role = jwt.getClaimAsString("role");
        var login = jwt.getClaimAsString("login");

        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    if (accountId != null) {
                        headers.set(ACCOUNT_ID_HEADER, accountId);
                    }
                    if (role != null) {
                        headers.set(ROLE_HEADER, role);
                    }
                    if (login != null) {
                        headers.set(LOGIN_HEADER, login);
                    }
                })
                .build();
        return exchange.mutate().request(mutatedRequest).build();
    }

    // Первое совпавшее правило выигрывает — тот же порядок приоритета, что у
    // requestMatchers(...) в сервисах: точный/узкий паттерн должен идти раньше широкого wildcard.
    private List<String> findAccess(ServerWebExchange exchange) {
        var path = PathContainer.parsePath(exchange.getRequest().getURI().getRawPath());
        var method = exchange.getRequest().getMethod();
        for (var rule : compiledRules) {
            if ((rule.method() == null || rule.method().equals(method)) && rule.pattern().matches(path)) {
                return rule.access();
            }
        }
        return defaultAccess == DefaultAccess.PUBLIC ? List.of(PUBLIC) : List.of(AUTHENTICATED);
    }

    private static String extractBearerToken(ServerWebExchange exchange) {
        var header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX.length());
    }

    // Тот же формат ответа, что у FallbackController — единообразные ошибки на всех
    // служебных путях gateway.
    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String detail) {
        log.warn("Authorization rejected: method={} uri={} status={} detail={}",
                exchange.getRequest().getMethod(), exchange.getRequest().getURI().getRawPath(), status, detail);

        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        byte[] body;
        try {
            body = jsonMapper.writeValueAsBytes(problem);
        } catch (Exception e) {
            body = new byte[0];
        }
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    private record CompiledRule(PathPattern pattern, HttpMethod method, List<String> access) {
    }
}
