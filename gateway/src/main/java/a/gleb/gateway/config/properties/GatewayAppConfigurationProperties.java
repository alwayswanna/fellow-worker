package a.gleb.gateway.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties("app")
public class GatewayAppConfigurationProperties {

    private List<RouteDefinition> routes = new ArrayList<>();

    private Authorization authorization = new Authorization(Authorization.DefaultAccess.AUTHENTICATED, List.of());

    public record RouteDefinition(String id, @NotNull URI uri) {}

    public record Authorization(DefaultAccess defaultAccess, List<Rule> rules) {

        /**
         * {@code method} — свободная строка ("GET", "POST", ...), резолвится в {@link
         * org.springframework.http.HttpMethod} в {@code AuthorizationFilter}, а не здесь, чтобы не
         * зависеть от relaxed-binding поддержки {@code HttpMethod} в {@code @ConfigurationProperties}.
         * {@code null} означает любой метод, как в {@code requestMatchers(String...)} без метода.
         */
        public record Rule(String path, String method, List<String> access) {}

        public enum DefaultAccess {PUBLIC, AUTHENTICATED}
    }
}
