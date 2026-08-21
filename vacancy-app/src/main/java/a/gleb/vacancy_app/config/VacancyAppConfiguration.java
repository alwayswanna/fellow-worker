package a.gleb.vacancy_app.config;

import a.gleb.vacancy_app.config.properties.VacancyAppConfigurationProperties;
import a.gleb.vacancy_app.constant.VacancyAppConstant;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@OpenAPIDefinition(
        info = @Info(
                title = "vacancy-app",
                description = "Microservice for vacancy management.",
                version = "1"
        )
)
@SecurityScheme(
        name = VacancyAppConstant.OAUTH_SECURITY_SCHEME,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${springdoc.app.authorizationUri}",
                        tokenUrl = "${springdoc.app.tokenUri}"
                )
        )
)
@Configuration
@EnableConfigurationProperties(VacancyAppConfigurationProperties.class)
@RequiredArgsConstructor
public class VacancyAppConfiguration {

    private final VacancyAppConfigurationProperties properties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(properties.unprotectedPatterns().toArray(String[]::new)).permitAll()
                        // "/vacancies/saved" matches the single-segment wildcard below, so it must be
                        // pinned to authenticated explicitly, ahead of that rule.
                        .requestMatchers(HttpMethod.GET, "/api/v1/vacancies/saved").authenticated()
                        // Guest browsing: vacancy search/detail is public; create/update/delete
                        // and everything under /applications stay behind auth.
                        .requestMatchers(HttpMethod.GET, "/api/v1/vacancies", "/api/v1/vacancies/*").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }
}
