/*
 * Copyright (c) 07-3/30/23, 10:34 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.user_service.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.Map;

/**
 *
 * @param cors                     CORS configuration {@link Cors}.
 * @param tokenUrl                 URI for receive authorization token.
 * @param issuerUrl                issuer url of token.
 * @param authorizationUrl         URI for receive authorization code.
 * @param encoderStrength          {@link org.springframework.security.crypto.password.PasswordEncoder} strength.
 * @param defaultRoles             array of roles, which will be created on application startup.
 * @param unprotectedPatterns      API patterns which permitted for all user.
 * @param defaultClients           array of default authorization client, which will be created on application startup.
 * @param removeExpiredSessionTask params of scheduled task, which clean up outdate sessions.
 * @param securityConstraints      configuration properties for protect service API.
 */
@Validated
@ConfigurationProperties("user-service")
public record OAuth2ServerConfigurationProperties(
        Cors cors,
        @NotBlank String tokenUrl,
        @NotBlank String issuerUrl,
        @NotBlank String authorizationUrl,
        @NotNull Integer encoderStrength,
        @NotNull List<DefaultRole> defaultRoles,
        @NotEmpty List<String> unprotectedPatterns,
        List<SecurityConstraints> securityConstraints,
        @NotNull Map<String, DefaultClient> defaultClients,
        @NotNull ScheduledTaskParams removeExpiredSessionTask
) {


    /**
     * Configuration for default registered client, which created on startup.
     *
     * @param clientId        client_id of registered client.
     * @param clientSecret    client_secret of registered client.
     * @param redirectUris    redirect_uris of registered client.
     * @param clientSecretTtl ttl for secret of registered client.
     * @param accessTokenTtl  default access token TTL.
     * @param refreshTokenTtl default refresh token TTL.
     */
    public record DefaultClient(
            @NotBlank String clientId,
            @NotBlank String clientSecret,
            @NotNull Long clientSecretTtl,
            @NotNull List<String> redirectUris,
            Long accessTokenTtl,
            Long refreshTokenTtl
    ) {

        public DefaultClient {
            accessTokenTtl = accessTokenTtl != 0 ? accessTokenTtl : 30;
            refreshTokenTtl = refreshTokenTtl != 0 ? refreshTokenTtl : 90;
        }
    }

    public static class Cors extends CorsConfiguration {
    }

    /**
     * Configuration of cleaner process, which remove session older than {@see dayOffset}
     *
     * @param dayOffset ttl for session.
     */
    public record ScheduledTaskParams(int dayOffset) {
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
     * @param systemRole           flag, that role allowed for all users or not
     */
    public record DefaultRole(boolean isMapOnDefaultClient, String roleName, String displayName, boolean systemRole) {
    }

    /**
     * Configuration for secure service`s API.
     *
     * @param roles               list of user roles.
     * @param securityCollections list of available mapping (method for pattern).
     */
    public record SecurityConstraints(
            @NotNull List<String> roles,
            @NotEmpty List<SecurityCollection> securityCollections
    ) {
    }

    /**
     * Configuration for some method, security of API.
     *
     * @param methods  list of HTTP methods, sample: POST, GET, HEAD... etc.
     * @param patterns list of service`s endpoints, sample: /api/v1/admin, /api/v1/user... etc.
     */
    public record SecurityCollection(List<String> methods, @NotEmpty List<String> patterns) {
    }
}
