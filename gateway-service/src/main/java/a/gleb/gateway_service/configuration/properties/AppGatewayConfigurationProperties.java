package a.gleb.gateway_service.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@ConfigurationProperties("fellow-worker-app-gateway")
public record AppGatewayConfigurationProperties(
        @NotNull
        Cors cors,
        String tokenUrl,
        String authorizationUrl,
        @NotEmpty
        List<String> unprotectedPatterns
) {

    public static class Cors extends CorsConfiguration {
    }
}
