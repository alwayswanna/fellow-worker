package a.gleb.oauth2server.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Setter
@Getter
@Validated
@ConfigurationProperties("oauth")
public class OAuth2ServerProperties {

    @NotBlank
    private String issueUrl;
    @NotNull
    private List<DefaultClient> defaultClients;
    @NotNull
    private DefaultUser defaultUser;
    private Cors cors;

    @Setter
    @Getter
    @Validated
    public static class DefaultUser {

        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String email;
    }

    @Getter
    @Setter
    @Validated
    public static class DefaultClient {

        @NotBlank
        private String defaultClientId;
        @NotBlank
        private String defaultClientSecret;
        @NotNull
        private List<String> defaultRedirectUris;

        private long defaultAccessTokenTimeToLive = 30;

        private long defaultRefreshTokenTimeToLive = 90;
    }

    public static class Cors extends CorsConfiguration {
    }
}
