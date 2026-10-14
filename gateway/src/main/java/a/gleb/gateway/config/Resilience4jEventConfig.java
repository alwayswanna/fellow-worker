package a.gleb.gateway.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Переводит переходы состояний circuit breaker/time limiter/bulkhead/rate limiter в логи.
 * Без этого о срабатывании защитных механизмов узнать можно только по метрикам в Prometheus
 * (которые ещё нужно догадаться построить) — здесь же причина деградации видна сразу в логах
 * рядом с остальными событиями запроса.
 * <p>
 * Instances создаются Spring Cloud Gateway/resilience4j лениво по мере обращения к роутам,
 * поэтому подписка идёт и на уже существующие инстансы, и на {@code onEntryAdded} реестра —
 * чтобы не пропустить те, что появятся позже.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class Resilience4jEventConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    @PostConstruct
    void registerEventConsumers() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::subscribe);
        circuitBreakerRegistry.getEventPublisher().onEntryAdded(event -> subscribe(event.getAddedEntry()));

        timeLimiterRegistry.getAllTimeLimiters().forEach(this::subscribe);
        timeLimiterRegistry.getEventPublisher().onEntryAdded(event -> subscribe(event.getAddedEntry()));

        bulkheadRegistry.getAllBulkheads().forEach(this::subscribe);
        bulkheadRegistry.getEventPublisher().onEntryAdded(event -> subscribe(event.getAddedEntry()));

        rateLimiterRegistry.getAllRateLimiters().forEach(this::subscribe);
        rateLimiterRegistry.getEventPublisher().onEntryAdded(event -> subscribe(event.getAddedEntry()));
    }

    private void subscribe(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn("circuit breaker '{}' state changed: {} -> {}",
                        circuitBreaker.getName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> log.warn("circuit breaker '{}' rejected call: OPEN",
                        circuitBreaker.getName()))
                .onFailureRateExceeded(event -> log.warn("circuit breaker '{}' failure rate exceeded: {}%",
                        circuitBreaker.getName(), event.getFailureRate()))
                .onSlowCallRateExceeded(event -> log.warn("circuit breaker '{}' slow-call rate exceeded: {}%",
                        circuitBreaker.getName(), event.getSlowCallRate()));
    }

    private void subscribe(TimeLimiter timeLimiter) {
        timeLimiter.getEventPublisher()
                .onTimeout(event -> log.warn("time limiter '{}' timed out a call", timeLimiter.getName()));
    }

    private void subscribe(Bulkhead bulkhead) {
        bulkhead.getEventPublisher()
                .onCallRejected(event -> log.warn("bulkhead '{}' rejected call: concurrency limit reached",
                        bulkhead.getName()));
    }

    private void subscribe(RateLimiter rateLimiter) {
        rateLimiter.getEventPublisher()
                .onFailure(event -> log.warn("rate limiter '{}' rejected call: limit exceeded",
                        rateLimiter.getName()));
    }
}
