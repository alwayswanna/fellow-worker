package a.gleb.gateway.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.JsonNodeFactory;

@Slf4j
@Component
public class RateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final JsonMapper jsonMapper;

    public RateLimiterGatewayFilterFactory(RateLimiterRegistry rateLimiterRegistry, JsonMapper jsonMapper) {
        super(Config.class);
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public @NonNull GatewayFilter apply(@NonNull Config config) {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(config.name());

        return (exchange, chain) -> chain.filter(exchange)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .onErrorResume(RequestNotPermitted.class, ex -> writeTooManyRequests(exchange, config.name()));
    }

    private Mono<Void> writeTooManyRequests(ServerWebExchange exchange, String routeId) {
        log.warn("rate limit exceeded for route '{}'", routeId);

        var problem = JsonNodeFactory.instance.objectNode()
                .put("status", HttpStatus.TOO_MANY_REQUESTS.value())
                .put("detail", "%s: rate limit exceeded, please retry later".formatted(routeId));

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        var bytes = jsonMapper.writeValueAsBytes(problem);
        var buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    public record Config(String name) {}
}
