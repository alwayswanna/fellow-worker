package a.gleb.appgateway.configuration;

import a.gleb.appgateway.configuration.properties.AppGatewayConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppGatewayConfigurationProperties.class)
public class AppGatewayConfiguration {
}
