package a.gleb.resume_app.config;

import a.gleb.resume_app.config.properties.ResumeAppConfigurationProperties;
import a.gleb.resume_app.constant.ResumeAppConstant;
import io.minio.MinioClient;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;

@OpenAPIDefinition(
        info = @Info(
                title = "resume-app",
                description = "Microservice for resume management.",
                version = "1"
        )
)
@SecurityScheme(
        name = ResumeAppConstant.OAUTH_SECURITY_SCHEME,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${springdoc.app.authorizationUri}",
                        tokenUrl = "${springdoc.app.tokenUri}"
                )
        )
)
@Configuration
@EnableConfigurationProperties(ResumeAppConfigurationProperties.class)
@EnableRetry
@EnableJpaAuditing
@RequiredArgsConstructor
public class ResumeAppConfig {

    private final ResumeAppConfigurationProperties properties;

    /**
     * Explicit timeouts so a slow/unreachable MinIO fails fast instead of hanging a request
     * indefinitely — the MinIO SDK's default OkHttp client has no connect/read deadline.
     */
    @Bean
    public MinioClient minioClient() {
        var minio = properties.minio();
        var httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(15))
                .writeTimeout(Duration.ofSeconds(15))
                .build();
        return MinioClient.builder()
                .endpoint(minio.endpoint())
                .credentials(minio.accessKey(), minio.secretKey())
                .httpClient(httpClient)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(properties.unprotectedPatterns().toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .build();
    }
}
