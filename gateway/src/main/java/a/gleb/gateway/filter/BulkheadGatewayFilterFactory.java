package a.gleb.gateway.filter;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class BulkheadGatewayFilterFactory extends AbstractGatewayFilterFactory<BulkheadGatewayFilterFactory.Config> {

    private final BulkheadRegistry bulkheadRegistry;

    public BulkheadGatewayFilterFactory(BulkheadRegistry bulkheadRegistry) {
        super(Config.class);
        this.bulkheadRegistry = bulkheadRegistry;
    }

    @Override
    public @NonNull GatewayFilter apply(@NonNull Config config) {
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(config.name());

        return (exchange, chain) -> chain.filter(exchange)
                .transformDeferred(BulkheadOperator.of(bulkhead));
    }

    public record Config(String name) {}
}
