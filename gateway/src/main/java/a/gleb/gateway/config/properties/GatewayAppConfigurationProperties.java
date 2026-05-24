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

    public record RouteDefinition(String id, @NotNull URI uri) {}
}
