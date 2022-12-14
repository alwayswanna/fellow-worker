package a.gleb.fellowworkerservice.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType.OAUTH2
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

const val OAUTH2_SECURITY_SCHEMA = "myOauth2Security"

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "client-manager-backend",
        description = "API service for manage resume/vacancy accounts",
        version = "v1"
    )
)
@SecurityScheme(
    name = OAUTH2_SECURITY_SCHEMA,
    type = OAUTH2,
    flows = OAuthFlows(
        authorizationCode = OAuthFlow(
            authorizationUrl = "\${springdoc.oAuthFlow.authorizationUrl}",
            tokenUrl = "\${springdoc.oAuthFlow.tokenUrl}",
            scopes = [OAuthScope(name = "openid", description = "openid scope")]
        )
    )
)
class OpenApiConfiguration {}