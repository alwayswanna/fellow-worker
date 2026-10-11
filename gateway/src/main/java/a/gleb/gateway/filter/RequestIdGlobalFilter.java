package a.gleb.gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Обеспечивает сквозной идентификатор запроса: переиспользует входящий заголовок,
 * если клиент/балансировщик его уже проставил, иначе генерирует новый, и прокидывает
 * его в ответ и в атрибут exchange — чтобы LoggingFilter и support могли сопоставить
 * запрос клиента с логами гейтвея без завязки на MDC в реактивном стеке.
 */
@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String REQUEST_ID_ATTR = "requestId";

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        var incoming = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        var requestId = StringUtils.hasText(incoming) ? incoming : UUID.randomUUID().toString();

        exchange.getAttributes().put(REQUEST_ID_ATTR, requestId);
        exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, requestId);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
