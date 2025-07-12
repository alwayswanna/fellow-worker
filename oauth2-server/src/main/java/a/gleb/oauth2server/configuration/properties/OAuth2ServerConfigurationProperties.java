/*
 * Copyright (c) 07-3/30/23, 10:34 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/**
 *
 * @param cors                     CORS configuration {@link Cors}.
 * @param tokenUrl                 URI for receive authorization token.
 * @param issuerUrl                issuer url of token.
 * @param authorizationUrl         URI for receive authorization code.
 * @param encoderStrength          {@link org.springframework.security.crypto.password.PasswordEncoder} strength.
 * @param rmq                      configuration for RabbitMQ integration
 * @param defaultRoles             array of roles, which will be created on application startup.
 * @param unprotectedPatterns      API patterns which permitted for all user.
 * @param defaultClients           array of default authorization client, which will be created on application startup.
 * @param removeExpiredSessionTask params of scheduled task, which clean up outdate sessions.
 * @param securityConstraints      configuration properties for protect service API.
 */
@Validated
@ConfigurationProperties("fellow-worker-oauth2-server")
public record OAuth2ServerConfigurationProperties(

        Cors cors,
        @NotBlank
        String tokenUrl,
        @NotBlank
        String issuerUrl,
        @NotBlank
        String authorizationUrl,
        @NotNull
        Integer encoderStrength,
        @NotNull
        RabbitMqConfiguration rmq,
        @NotNull
        List<DefaultRole> defaultRoles,
        @NotEmpty
        List<String> unprotectedPatterns,
        @NotNull
        List<DefaultClient> defaultClients,
        @NotNull
        ScheduledTaskParams removeExpiredSessionTask,
        List<SecurityConstraints> securityConstraints
) {


    /**
     * Configuration for default registered client, which created on startup.
     *
     * @param defaultClientId               client_id of registered client.
     * @param defaultClientSecret           client_secret of registered client.
     * @param defaultRedirectUris           redirect_uris of registered client.
     * @param clientSecretDaysTtl           ttl for secret of registered client.
     * @param defaultAccessTokenTimeToLive  default access token TTL.
     * @param defaultRefreshTokenTimeToLive default refresh token TTL.
     */
    public record DefaultClient(
            @NotBlank
            String defaultClientId,
            @NotBlank
            String defaultClientSecret,
            @NotNull
            Long clientSecretDaysTtl,
            @NotNull
            List<String> defaultRedirectUris,
            long defaultAccessTokenTimeToLive,
            long defaultRefreshTokenTimeToLive
    ) {

        public DefaultClient {
            defaultAccessTokenTimeToLive = defaultAccessTokenTimeToLive != 0 ? defaultAccessTokenTimeToLive : 30;
            defaultRefreshTokenTimeToLive = defaultRefreshTokenTimeToLive != 0 ? defaultRefreshTokenTimeToLive : 90;
        }
    }

    public static class Cors extends CorsConfiguration {
    }

    /**
     * Configuration of cleaner process, which remove session older than {@see dayOffset}
     *
     * @param dayOffset ttl for session.
     */
    public record ScheduledTaskParams(
            int dayOffset
    ) {
        public ScheduledTaskParams {
            dayOffset = dayOffset != 0 ? dayOffset : 30;
        }
    }

    /**
     * Default role settings which created on startup.
     *
     * @param isMapOnDefaultClient map to default client
     * @param roleName             name of role
     * @param displayName          display role`s name
     */
    public record DefaultRole(
            boolean isMapOnDefaultClient,
            String roleName,
            String displayName
    ) {
    }

    /**
     * RabbitMQ configuration.
     *
     * @param awaitTimeout timeout for waiting ack from RabbitMQ while send witch 'publish-confirms'.
     */
    public record RabbitMqConfiguration(
            @NotNull
            Integer awaitTimeout
    ) {
    }

    /**
     * Configuration for secure service`s API.
     *
     * @param roles               list of user roles.
     * @param securityCollections list of available mapping (method for pattern).
     */
    public record SecurityConstraints(
            @NotNull
            List<String> roles,
            @NotEmpty
            List<SecurityCollection> securityCollections
    ) {

    }

    /**
     * Configuration for some method, security of API.
     *
     * @param methods  list of HTTP methods, sample: POST, GET, HEAD... etc.
     * @param patterns list of service`s endpoints, sample: /api/v1/admin, /api/v1/user... etc.
     */
    public record SecurityCollection(
            List<String> methods,
            @NotEmpty
            List<String> patterns
    ) {
    }
}
