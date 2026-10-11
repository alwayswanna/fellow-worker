package a.gleb.gateway.controller;

import a.gleb.gateway.filter.RequestIdGlobalFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/{serviceId}")
    public ResponseEntity<ProblemDetail> fallback(@PathVariable String serviceId, ServerWebExchange exchange) {
        var requestId = exchange.getAttributeOrDefault(RequestIdGlobalFilter.REQUEST_ID_ATTR, "unknown");
        Throwable cause = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);

        log.warn("[{}] fallback triggered for service '{}', cause: {}", requestId, serviceId,
                cause == null ? "unknown (bulkhead/rate-limit rejection or direct fallback call)" : cause.toString());

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "%s is temporarily unavailable, please retry later".formatted(serviceId)
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problem);
    }
}
