package a.gleb.user_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static a.gleb.user_service.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

@OpenAPIDefinition(
        info = @Info(
                title = "user-service",
                description = "Service for users authentication & authorization. API user management & authorities management. ",
                version = "v1"
        )
)
@SecurityScheme(
        name = OAUTH2_SERVER_DEFINITION,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${user-service.authorization-url}",
                        tokenUrl = "${user-service.token-url}",
                        scopes = {
                                @OAuthScope(
                                        name = "openid", description = "Scope for get additional information about user."
                                )
                        }
                ),
                clientCredentials = @OAuthFlow(
                        tokenUrl = "${user-service.token-url}"
                )
        )
)
public record OpenApiOAuth2ServerConfiguration() {/* dummy class*/}
