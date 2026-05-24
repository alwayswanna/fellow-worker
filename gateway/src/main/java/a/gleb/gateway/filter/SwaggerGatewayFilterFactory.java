package a.gleb.gateway.filter;

import a.gleb.gateway.model.FilterConfig;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("swaggerApiFilterFactory")
public class SwaggerGatewayFilterFactory extends AbstractGatewayFilterFactory<FilterConfig> {

    private static final String URL_FIELD = "url";
    private static final String SERVERS_FIELD = "servers";

    private final JsonMapper jsonMapper;
    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBody;

    public SwaggerGatewayFilterFactory(
            JsonMapper jsonMapper,
            ModifyResponseBodyGatewayFilterFactory modifyResponseBody
    ) {
        super(FilterConfig.class);
        this.jsonMapper = jsonMapper;
        this.modifyResponseBody = modifyResponseBody;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("urls");
    }

    @Override
    public @NonNull GatewayFilter apply(@NonNull FilterConfig config) {
        return modifyResponseBody.apply(c ->
                c.setRewriteFunction(
                        String.class,
                        String.class,
                        (_, body) -> replaceServers(config, body)
                )
        );
    }

    private Mono<String> replaceServers(FilterConfig config, String body) {

        if (!StringUtils.hasText(body)) {
            return Mono.just(body);
        }

        try {
            var parsed = jsonMapper.readTree(body.getBytes());
            if (!(parsed instanceof ObjectNode root)) {
                return Mono.just(body);
            }

            List<String> urls = config.urls();
            if (CollectionUtils.isEmpty(urls)) {
                return Mono.just(body);
            }

            return replaceServers(root, urls);
        } catch (Exception e) {
            log.error("OpenApiServer: не удалось переписать servers", e);
            return Mono.just(body);
        }
    }

    private Mono<String> replaceServers(ObjectNode root, List<String> urls) throws JacksonException {
        ArrayNode servers = urls.stream()
                .map(u -> jsonMapper.createObjectNode().put(URL_FIELD, u))
                .collect(Collectors.collectingAndThen(Collectors.toList(), jsonMapper::valueToTree));

        root.set(SERVERS_FIELD, servers);

        return Mono.just(jsonMapper.writeValueAsString(root));
    }
}
