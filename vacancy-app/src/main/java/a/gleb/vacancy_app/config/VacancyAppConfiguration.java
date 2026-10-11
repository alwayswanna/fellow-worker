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
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import java.time.Duration;

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

    /**
     * Used by {@link a.gleb.vacancy_app.adapter.out.client.CompanyRestClientAdapter} to enrich
     * vacancy responses with company display data and to authorize vacancy/application
     * management. Explicit timeouts so a slow/unreachable company-app fails fast instead of
     * hanging the calling request indefinitely (the JDK HTTP client has no default timeout).
     */
    @Bean
    public RestClient companyServiceRestClient() {
        var settings = HttpClientSettings.defaults().withTimeouts(Duration.ofSeconds(2), Duration.ofSeconds(3));
        return RestClient.builder()
                .baseUrl(properties.companyService().baseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }

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
