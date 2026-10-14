package a.gleb.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Collections;

import static a.gleb.gateway.filter.RequestIdGlobalFilter.REQUEST_ID_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var start = System.currentTimeMillis();

        var method = exchange.getRequest().getMethod();
        var requestId = exchange.getAttributeOrDefault(REQUEST_ID_ATTR, "unknown");

        var uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
        var originalUri = (uris.isEmpty()) ? "unknown" : uris.iterator().next().toString();

        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        var routeId = route == null ? "unknown" : route.getId();

        var routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

        log.info("[{}] incoming request: {} {} is routed to {} {}", requestId, method, originalUri, routeId, routeUri);

        return chain.filter(exchange)
                .doOnError(ex -> log.warn("[{}] request failed after {}ms: {}", requestId,
                        System.currentTimeMillis() - start, ex.toString()))
                .doFinally(signalType -> {
                    var duration = System.currentTimeMillis() - start;

                    switch (signalType) {
                        case CANCEL -> log.info("[{}] request canceled: duration={}ms", requestId, duration);
                        // Ошибка уже подробно залогирована в doOnError с деталями причины.
                        case ON_ERROR -> {
                        }
                        default -> {
                            var response = exchange.getResponse();
                            log.info("[{}] outgoing response: status={}, duration={}ms",
                                    requestId, response.getStatusCode(), duration);
                        }
                    }
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
