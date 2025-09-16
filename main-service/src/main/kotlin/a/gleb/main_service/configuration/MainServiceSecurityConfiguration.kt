/*
 * Copyright (c) 12-3/25/23, 8:21 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.main_service.configuration

import a.gleb.main_service.configuration.properties.MainServiceConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

const val ROLE_PREFIX: String = "ROLE_"
const val ROLE_KEY_CLAIM: String = "role"
const val SCOPE_KEY_CLAIM: String = "scope"
const val SCOPE_PREFIX: String = "SCOPE_"
const val SCOPE_AUTHORITY: String = "SCOPE_openid"

val unauthorizedPatterns = listOf(
    "/swagger-ui.html",
    "/webjars/swagger-ui/index.html",
    "/webjars/swagger-ui/**",
    "/webjars/**",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/actuator/**",
    "/error"
)


@Configuration
@EnableWebFluxSecurity
@EnableR2dbcRepositories
class MainServiceSecurityConfiguration(
    private var properties: MainServiceConfigurationProperties
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange { it.pathMatchers("/**").permitAll() }
            .oauth2ResourceServer { it.jwt { converter ->
                val reactiveJwtAuthenticationConverter = ReactiveJwtAuthenticationConverter()
                reactiveJwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                    ReactiveJwtGrantedAuthoritiesConverterAdapter(OAuth2ResourceServerConverter())
                )
                converter.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter)
            } }
            .build()
    }

    /**
     * Converter for JWT from user-service.
     */
    class OAuth2ResourceServerConverter: Converter<Jwt, Collection<GrantedAuthority>> {
        override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
            return listOf(SimpleGrantedAuthority(jwt.subject))
        }
    }
}
