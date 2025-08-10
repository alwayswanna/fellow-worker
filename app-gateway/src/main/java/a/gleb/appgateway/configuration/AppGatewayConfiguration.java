package a.gleb.appgateway.configuration;

import a.gleb.appgateway.configuration.properties.AppGatewayConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(AppGatewayConfigurationProperties.class)
public class AppGatewayConfiguration {

    private final AppGatewayConfigurationProperties properties;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(this::configureCors)
                .authorizeExchange(this::configureAuthorizationHttpRequests)
                .oauth2ResourceServer(it -> it.jwt(Customizer.withDefaults()))
                .build();
    }

    private void configureAuthorizationHttpRequests(AuthorizeExchangeSpec exchangeSpec) {
        exchangeSpec
                .pathMatchers(properties.unprotectedPatterns().toArray(String[]::new))
                .permitAll();
        exchangeSpec.anyExchange().authenticated();
    }

    private void configureCors(CorsSpec corsSpec) {
        var configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", properties.cors());
        corsSpec.configurationSource(configurationSource);
    }
}
