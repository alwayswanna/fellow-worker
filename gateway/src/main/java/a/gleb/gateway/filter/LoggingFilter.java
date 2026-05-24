package a.gleb.gateway.filter;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Единый формат лога на запрос — "Request completed: method=... uri=... status=... ...",
 * тот же, что у {@code RequestLoggingFilter} в downstream-сервисах (vacancy-app/user-app/...),
 * включая пустой requestor: у gateway нет собственной аутентификации, поле оставлено для
 * единообразной формы строки между сервисами (проще парсить в ELK/Loki одним паттерном).
 */
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    // Совпадает с a.gleb.vacancy_app.constant.VacancyAppConstant.TRACE_ID_RESPONSE_HEADER —
    // одно и то же имя заголовка, чтобы клиент и downstream-сервисы видели идентичный traceId.
    public static final String TRACE_ID_RESPONSE_HEADER = "X-Trace-ID";

    // Агрегированные OpenAPI-роуты (см. GatewayAppConfiguration.getApiDocsRoute) всегда
    // заканчиваются на этот путь независимо от routeId — по нему и фильтруем.
    // Prometheus/actuator сюда намеренно не включены: они слушают отдельный
    // management.server.port и до этого GlobalFilter физически не доходят.
    private static final String API_DOCS_PATH = "/v3/api-docs";

    private static final String SPAN_NAME = "gateway.request";
    private static final String UNKNOWN_REMOTE_ADDR = "unknown";
    private static final String NO_REQUESTOR = "";

    private final Tracer tracer;
    private final Propagator propagator;

    public LoggingFilter(Tracer tracer, Propagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @Override
    public @NonNull Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        if (exchange.getRequest().getURI().getPath().endsWith(API_DOCS_PATH)) {
            return chain.filter(exchange);
        }

        var start = System.currentTimeMillis();
        var method = exchange.getRequest().getMethod();
        var uri = requestUri(exchange);
        var remoteAddr = remoteAddress(exchange);

        // Gateway — точка входа и граница доверия: сам создаёт корневой span для каждого
        // запроса, а не продолжает trace-контекст, который мог прислать внешний клиент.
        var span = tracer.spanBuilder().name(SPAN_NAME).setNoParent().start();
        var traceId = span.context().traceId();

        exchange.getResponse().getHeaders().add(TRACE_ID_RESPONSE_HEADER, traceId);

        var mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    // Полный w3c/b3-контекст (включая spanId как parent-id) — чтобы downstream-
                    // сервисы, участвующие в трейсинге, продолжали ту же цепочку спанов.
                    propagator.inject(span.context(), headers, HttpHeaders::set);
                    // Отдельный заголовок с одним только traceId — для сквозной корреляции
                    // логов там, где трейсинг не нужен, без разбора traceparent.
                    headers.set(TRACE_ID_RESPONSE_HEADER, traceId);
                })
                .build();

        var loggingResponse = new ServerHttpResponseDecorator(exchange.getResponse()) {
            @Override
            public @NonNull Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                var statusCode = getStatusCode();
                if (statusCode == null || statusCode.is2xxSuccessful()) {
                    return super.writeWith(body);
                }

                // join склеивает поток в один буфер, а toString(Charset) читает его без сдвига
                // позиции чтения — поэтому тот же буфер можно безопасно отдать дальше на запись.
                return super.writeWith(DataBufferUtils.join(body)
                        .doOnNext(buffer -> logInScope(span, () -> log.warn(
                                "Error response body: method={} uri={} status={} body={}",
                                method, uri, statusCode, buffer.toString(StandardCharsets.UTF_8)))));
            }
        };

        var mutatedExchange = exchange.mutate().request(mutatedRequest).response(loggingResponse).build();

        return chain.filter(mutatedExchange)
                .doOnError(ex -> logInScope(span, () -> log.warn(
                        "Request failed: method={} uri={} duration={}ms requestor={} remoteAddr={} error={}",
                        method, uri, System.currentTimeMillis() - start, NO_REQUESTOR, remoteAddr, ex.toString())))
                .doFinally(signalType -> {
                    var duration = System.currentTimeMillis() - start;

                    switch (signalType) {
                        case CANCEL -> logInScope(span, () -> log.info(
                                "Request canceled: method={} uri={} duration={}ms requestor={} remoteAddr={}",
                                method, uri, duration, NO_REQUESTOR, remoteAddr));
                        // Ошибка уже подробно залогирована в doOnError с деталями причины.
                        case ON_ERROR -> {
                        }
                        default -> {
                            var status = exchange.getResponse().getStatusCode();
                            logInScope(span, () -> log.info(
                                    "Request completed: method={} uri={} status={} duration={}ms requestor={} remoteAddr={}",
                                    method, uri, status, duration, NO_REQUESTOR, remoteAddr));
                        }
                    }

                    span.end();
                });
    }

    // Делает span "текущим" для OTel Context ровно на момент одного лог-вызова.
    // OpenTelemetryAppender (см. logback-spring.xml) читает Context.current() синхронно
    // в момент записи строки лога, поэтому короткого scope достаточно независимо от того,
    // на каком event-loop-потоке реально выполняется этот конкретный сигнал Reactor —
    // в отличие от обычного MDC.put, который не переживает прыжки между потоками.
    private void logInScope(Span span, Runnable logCall) {
        try (var scope = tracer.withSpan(span)) {
            logCall.run();
        }
    }

    private static String requestUri(ServerWebExchange exchange) {
        var uri = exchange.getRequest().getURI();
        var query = uri.getQuery();
        return query != null ? "%s?%s".formatted(uri.getRawPath(), query) : uri.getRawPath();
    }

    private static String remoteAddress(ServerWebExchange exchange) {
        var remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress == null || remoteAddress.getAddress() == null
                ? UNKNOWN_REMOTE_ADDR
                : remoteAddress.getAddress().getHostAddress();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
