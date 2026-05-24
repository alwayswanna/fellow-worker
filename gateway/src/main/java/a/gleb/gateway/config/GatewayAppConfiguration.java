package a.gleb.gateway.config;

import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties;
import a.gleb.gateway.config.properties.GatewayAppConfigurationProperties.RouteDefinition;
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

import java.util.List;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GatewayAppConfigurationProperties.class)
public class GatewayAppConfiguration {

    private static final String API_DOCS_ROUTE_POSTFIX = "-api";

    private static final String MATCH_ALL_PATTERN = "/**";
    private static final String API_DOCS_PATH = "/v3/api-docs";

    private static final Profiles NOT_PROD_PROFILES_EXPR = Profiles.of("!prod");

    private final Environment env;
    private final GatewayAppConfigurationProperties properties;
    private final SwaggerGatewayFilterFactory openApiServerFilterFactory;

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
        return r -> r
                .path("/" + crmRouteDefinition.id() + MATCH_ALL_PATTERN)
                .filters(f -> f.stripPrefix(1))
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
