package a.gleb.gateway.config;

import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties;
import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties.RouteDefinition;
import a.gleb.gateway.filter.BulkheadGatewayFilterFactory;
import a.gleb.gateway.filter.RateLimiterGatewayFilterFactory;
import a.gleb.gateway.filter.SwaggerGatewayFilterFactory;
import a.gleb.gateway.model.FilterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus.Series;

import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GatewayAppConfigurationProperties.class)
public class GatewayAppConfiguration {

    private static final String API_DOCS_ROUTE_POSTFIX = "-api";

    private static final String MATCH_ALL_PATTERN = "/**";
    private static final String API_DOCS_PATH = "/v3/api-docs";
    private static final String FALLBACK_PATH = "/fallback/";
    private static final int RETRY_ATTEMPTS = 2;

    // Экспоненциальный backoff между ретраями: 100ms -> 200ms, плюс джиттер,
    // чтобы одновременный всплеск ретраев с разных клиентов не превращался в retry storm.
    private static final Duration RETRY_FIRST_BACKOFF = Duration.ofMillis(100);
    private static final Duration RETRY_MAX_BACKOFF = Duration.ofMillis(500);
    private static final int RETRY_BACKOFF_FACTOR = 2;
    private static final double RETRY_JITTER_FACTOR = 0.5;

    private static final Profiles NOT_PROD_PROFILES_EXPR = Profiles.of("!prod");

    private final Environment env;
    private final GatewayAppConfigurationProperties properties;
    private final SwaggerGatewayFilterFactory openApiServerFilterFactory;
    private final BulkheadGatewayFilterFactory bulkheadGatewayFilterFactory;
    private final RateLimiterGatewayFilterFactory rateLimiterGatewayFilterFactory;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        properties.getRoutes()
                .forEach(crmRouteDefinition -> {
                    routes.route(crmRouteDefinition.id(), getRoute(crmRouteDefinition));

                    if (isNotProduction()) {
                        routes.route(crmRouteDefinition.id() + API_DOCS_ROUTE_POSTFIX, getApiDocsRoute(crmRouteDefinition));
                    }
                });

        return routes.build();
    }

    private boolean isNotProduction() {
        return env.acceptsProfiles(NOT_PROD_PROFILES_EXPR);
    }

    private Function<PredicateSpec, Buildable<Route>> getRoute(RouteDefinition crmRouteDefinition) {
        var routeId = crmRouteDefinition.id();
        return r -> r
                .path("/" + routeId + MATCH_ALL_PATTERN)
                .filters(f -> f
                        .stripPrefix(1)
                        // Rate limiter стоит первым (снаружи), чтобы отклонённые по лимиту запросы
                        // не попадали в circuit breaker/retry/bulkhead и не искажали их статистику.
                        .filter(rateLimiterGatewayFilterFactory.apply(new RateLimiterGatewayFilterFactory.Config(routeId)))
                        .circuitBreaker(c -> c
                                .setName(routeId)
                                .setFallbackUri(FALLBACK_PATH + routeId))
                        .retry(retryConfig -> retryConfig
                                .setRetries(RETRY_ATTEMPTS)
                                .setMethods(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
                                .setSeries(Series.SERVER_ERROR)
                                .setExceptions(IOException.class, ConnectException.class, TimeoutException.class)
                                .setBackoff(RETRY_FIRST_BACKOFF, RETRY_MAX_BACKOFF, RETRY_BACKOFF_FACTOR, true)
                                .setJitter(RETRY_JITTER_FACTOR))
                        // Bulkhead стоит последним (ближе к реальному вызову), ограничивая число
                        // одновременных запросов к конкретному downstream-сервису.
                        .filter(bulkheadGatewayFilterFactory.apply(new BulkheadGatewayFilterFactory.Config(routeId))))
                .uri(crmRouteDefinition.uri());
    }

    private Function<PredicateSpec, Buildable<Route>> getApiDocsRoute(RouteDefinition crmRouteDefinition) {
        return r -> r
                .order(-1)
                .path("/" + crmRouteDefinition.id() + API_DOCS_PATH)
                .filters(f -> f
                        .stripPrefix(1)
                        .filter(openApiServerFilterFactory.apply(
                                new FilterConfig(
                                        List.of("/" + crmRouteDefinition.id())
                                )
                        )))
                .uri(crmRouteDefinition.uri());
    }

}
