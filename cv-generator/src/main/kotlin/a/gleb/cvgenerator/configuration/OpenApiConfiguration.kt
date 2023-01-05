/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme

const val OAUTH2_SECURITY_SCHEMA = "myOauth2Security"

@OpenAPIDefinition(
    info = Info(
        title = "cv-generator-backend",
        description = "API service for manage user PDF files.",
        version = "v1"
    )
)
@SecurityScheme(
    name = OAUTH2_SECURITY_SCHEMA,
    type = SecuritySchemeType.OAUTH2,
    flows = OAuthFlows(
        authorizationCode = OAuthFlow(
            authorizationUrl = "\${springdoc.oAuthFlow.authorizationUrl}",
            tokenUrl = "\${springdoc.oAuthFlow.tokenUrl}",
            scopes = [OAuthScope(name = "openid", description = "openid scope")]
        )
    )
)
class OpenApiConfiguration {
}