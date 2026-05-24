package a.gleb.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.Collections;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter {

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var start = System.currentTimeMillis();

        var method = exchange.getRequest().getMethod();

        var uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
        var originalUri = (uris.isEmpty()) ? "unknown" : uris.iterator().next().toString();

        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        var routeId = route == null ? "unknown" : route.getId();

        var routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);

        log.info("incoming request: {} {} is routed to {} {}", method, originalUri, routeId, routeUri);

        return chain.filter(exchange).doFinally(signalType -> {
            var duration = System.currentTimeMillis() - start;

            if (SignalType.CANCEL.equals(signalType)) {
                log.info("request canceled: duration={}ms", duration);
                return;
            }

            var response = exchange.getResponse();
            log.info("outgoing response: status={}, duration={}ms", response.getStatusCode(), duration);
        });
    }
}
