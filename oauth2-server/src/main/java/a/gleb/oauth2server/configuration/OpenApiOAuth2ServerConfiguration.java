package a.gleb.oauth2server.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

@OpenAPIDefinition(
        info = @Info(
                title = "oauth2-server",
                description = "Service for users authentication & authorization. API for manager users & users authorities. ",
                version = "v1"
        )
)
@SecurityScheme(
        name = OAUTH2_SERVER_DEFINITION,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${fellow-worker-oauth2-server.authorization-url}",
                        tokenUrl = "${fellow-worker-oauth2-server.token-url}",
                        scopes = {
                                @OAuthScope(
                                        name = "openid", description = "Scope for get additional information about user."
                                )
                        }
                ),
                clientCredentials = @OAuthFlow(
                        tokenUrl = "${fellow-worker-oauth2-server.token-url}"
                )
        )
)
public record OpenApiOAuth2ServerConfiguration() {/* dummy class*/}
