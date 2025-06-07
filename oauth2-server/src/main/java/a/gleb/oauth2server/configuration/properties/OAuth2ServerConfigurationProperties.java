/*
 * Copyright (c) 07-3/30/23, 10:34 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@ConfigurationProperties("fellow-worker-oauth2-server")
public class OAuth2ServerConfigurationProperties {

    @NotBlank
    private String tokenUrl;

    @NotBlank
    private String authorizationUrl;

    @NotBlank
    private String issuerUrl;

    @NotNull
    private List<DefaultClient> defaultClients;

    private Cors cors;

    @NotNull
    private Integer encoderStrength;

    @NotNull
    private ScheduledTaskParams removeExpiredSessionTask;

    @NotEmpty
    private List<String> unprotectedPatterns;

    private List<SecurityConstraints> securityConstraints;

    @NotNull
    private RabbitMqConfiguration rmq;

    @NotNull
    private List<DefaultRole> defaultRoles;

    @Getter
    @Setter
    public static class DefaultClient {

        @NotBlank
        private String defaultClientId;
        @NotBlank
        private String defaultClientSecret;
        @NotNull
        private List<String> defaultRedirectUris;
        @NotNull
        private Long clientSecretDaysTtl;

        private long defaultAccessTokenTimeToLive = 30;

        private long defaultRefreshTokenTimeToLive = 90;
    }

    public static class Cors extends CorsConfiguration {
    }

    @Getter
    @Setter
    public static class ScheduledTaskParams {

        int dayOffset;
    }

    @Getter
    @Setter
    public static class SecurityConstraints {

        @NotNull
        private List<String> roles;

        @NotEmpty
        private List<SecurityCollection> securityCollections;

    }

    @Getter
    @Setter
    public static class SecurityCollection {

        private List<String> methods;

        @NotEmpty
        private List<String> patterns;
    }

    @Getter
    @Setter
    public static class RabbitMqConfiguration {

        @NotNull
        Integer awaitTimeout;
    }

    /**
     * Default role settings which created on startup.
     * @param isMapOnDefaultClient map to default client
     * @param roleName name of role
     * @param displayName display role`s name
     */
    public record DefaultRole(
            boolean isMapOnDefaultClient,
            String roleName,
            String displayName
    ) {
    }
}
