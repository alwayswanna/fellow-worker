package a.gleb.appgateway.configuration;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static a.gleb.appgateway.constants.AppGatewayConstants.OAUTH2_SERVER_DEFINITION;

@OpenAPIDefinition(
        info = @Info(
                title = "app-gateway",
                description = "Gateway service for all servers. ",
                version = "v1"
        )
)
@SecurityScheme(
        name = OAUTH2_SERVER_DEFINITION,
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "${fellow-worker-app-gateway.authorization-url}",
                        tokenUrl = "${fellow-worker-app-gateway.token-url}",
                        scopes = {
                                @OAuthScope(
                                        name = "openid", description = "Scope for get additional information about user."
                                )
                        }
                ),
                clientCredentials = @OAuthFlow(
                        tokenUrl = "${fellow-worker-app-gateway.token-url}"
                )
        )
)
public record OpenApiAppGatewayConfiguration() {
}
