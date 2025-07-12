package a.gleb.appgateway.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fellow-worker-app-gateway")
public record AppGatewayConfigurationProperties (

){
}
