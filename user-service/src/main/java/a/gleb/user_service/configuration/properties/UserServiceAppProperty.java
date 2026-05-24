package a.gleb.user_service.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties("us")
public record UserServiceAppProperty(
        @NotNull Cors cors,
        @NotNull Integer encryptionStrength,
        @NotNull List<OAuth2Client> clients,
        @NotEmpty List<String> unprotectedPatterns
) {

    public static class Cors extends CorsConfiguration {}

    public record OAuth2Client(
            @NotBlank String clientId,
            @NotBlank String clientSecret,
            @NotEmpty List<String> redirectUris,
            @NotNull Duration accessTokenTtl,
            @NotNull Duration refreshTokenTtl
    ){}
}
