package a.gleb.appgateway.configuration;

import a.gleb.appgateway.configuration.properties.AppGatewayConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(AppGatewayConfigurationProperties.class)
public class AppGatewayConfiguration {

    private final AppGatewayConfigurationProperties properties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(it -> {
                    var configurationSource = new UrlBasedCorsConfigurationSource();
                    configurationSource.registerCorsConfiguration("/**", properties.cors());
                    it.configurationSource(configurationSource);
                })
                .authorizeHttpRequests(this::configureAuthorizationHttpRequests)
                .oauth2ResourceServer(Customizer.withDefaults())
                .build();
    }

    private void configureAuthorizationHttpRequests(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        var unprotectedPatterns = properties.unprotectedPatterns().toArray(String[]::new);

        registry.requestMatchers(unprotectedPatterns).permitAll();
        registry.anyRequest().authenticated();
    }

}
