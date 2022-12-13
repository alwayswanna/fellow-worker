package a.gleb.clientmanager.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static a.gleb.clientmanager.configuration.OpenApiConfiguration.OAUTH2_SECURITY_SCHEMA;

@OpenAPIDefinition(info = @Info(title = "client-manager-backend",
        description = "API service for manage client accounts", version = "v1"))
@SecurityScheme(name = OAUTH2_SECURITY_SCHEMA, type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}"
                , tokenUrl = "${springdoc.oAuthFlow.tokenUrl}", scopes = {
                @OAuthScope(name = "openid", description = "openid scope")})))
public record OpenApiConfiguration() {

    public static final String OAUTH2_SECURITY_SCHEMA = "myOauth2Security";
}
