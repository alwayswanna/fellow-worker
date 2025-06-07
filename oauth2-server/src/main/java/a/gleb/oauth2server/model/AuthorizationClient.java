package a.gleb.oauth2server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class AuthorizationClient {

    @Schema(description = "Model for create new registered client or edit existing.")
    public record AuthorizationClientRequest(
            @Schema(description = "ClientID for make authorization request.")
            String clientId,
            @Schema(description = "ClientSecret for make authorization request.")
            String clientSecret,
            @Schema(description = "DateTime when, ClientSecret will be invalid.")
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDateTime clientSecretExpiredAt,
            @Schema(description = "Name of client.")
            String clientName,
            @Schema(description = "Available methods for authorization.")
            List<String> authenticationMethods,
            @Schema(description = "Available types for authorization.")
            List<String> authenticationGrantTypes,
            @Schema(description = "Redirect uris after successfully authorization.")
            List<String> redirectUris,
            @Schema(description = "Redirect uris on logout.")
            List<String> postLogoutRedirectUris,
            @Schema(description = "Available scopes for authorization.")
            List<String> scopes,
            @Schema(description = "Access token TTL in minutes")
            Integer accessTokenTtl,
            @Schema(description = "Refresh token TTL in minutes")
            Integer refreshTokenTtl

    ) {
    }

    @Schema(description = "Model for show information about existing client. ")
    public record AuthorizationClientResponse(
            @Schema(description = "Client ID for authorization")
            String clientId,

            @Schema(description = "Client secret for authorization")
            String encodedSecret,

            @Schema(description = "List of redirect urls")
            List<String> redirectUrls,

            @Schema(description = "Authentication methods")
            List<String> clientAuthenticationMethods,

            @Schema(description = "Available grant types")
            List<String> authorizationGrantTypes,

            @Schema(description = "Available scoped")
            List<String> scopes,

            @Schema(description = "Redirects after logout")
            List<String> postLogoutRedirectUrls
    ) {
    }
}
