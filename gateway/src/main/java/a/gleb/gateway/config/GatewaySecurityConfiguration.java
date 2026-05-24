package a.gleb.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Подключение {@code spring-boot-starter-oauth2-resource-server} триггерит reactive security
 * autoconfig Spring Boot, который по умолчанию добавляет deny-by-default
 * {@link SecurityWebFilterChain}. Реальные решения PUBLIC/AUTHENTICATED/роль принимает
 * {@link a.gleb.gateway.filter.AuthorizationFilter} (нужен именно динамический YAML-driven
 * список правил, а не статический DSL) — здесь только permitAll-чейн, чтобы погасить
 * автоконфигурацию и не блокировать всё дважды.
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }
}
