/*
 * Copyright (c) 12-3/25/23, 8:21 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.configuration

import a.gleb.fellowworkerservice.configuration.properties.FellowWorkerConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Flux

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
class FellowWorkerSecurityConfiguration(
    private var properties: FellowWorkerConfigurationProperties
) {

    @Bean
    fun oauth2SecurityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        httpSecurity
            .anonymous().disable()
            .csrf().disable()
            .authorizeExchange { restrictApiMethods(it) }
            .oauth2ResourceServer()
            .jwt {
                val reactiveJwtConverter = ReactiveJwtAuthenticationConverter()
                reactiveJwtConverter.setJwtGrantedAuthoritiesConverter(SpringBootReactiveJwtConverter())

                it.jwtAuthenticationConverter(reactiveJwtConverter)
            }
        httpSecurity.cors {
            it.configurationSource(corsConfiguration(properties))
        }

        return httpSecurity.build()
    }

    @Bean
    fun corsConfiguration(properties: FellowWorkerConfigurationProperties): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", properties.cors)
        return source
    }

    private fun restrictApiMethods(authorizeExchangeSpec: ServerHttpSecurity.AuthorizeExchangeSpec) {
        properties.securityConstraints.forEach {
            val roles = it.roles.toTypedArray()
            it.securityCollections.forEach { securityCollection ->
                val patterns = securityCollection.patterns.toTypedArray()
                securityCollection.methods?.forEach { method ->
                    authorizeExchangeSpec.pathMatchers(HttpMethod.valueOf(method), *patterns).hasAnyRole(*roles)
                } ?: authorizeExchangeSpec.pathMatchers(*patterns).hasAnyRole(*roles)
            }
        }

        val scopedPaths = properties.scopedPaths.toTypedArray()
        authorizeExchangeSpec.pathMatchers(*scopedPaths).hasAuthority(SCOPE_AUTHORITY)

        val unprotectedPaths = properties.unprotectedPaths
        val permitAllMappings = (unprotectedPaths + unauthorizedPatterns).toTypedArray()

        authorizeExchangeSpec.pathMatchers(*permitAllMappings).permitAll()
    }
}

class SpringBootReactiveJwtConverter : Converter<Jwt, Flux<GrantedAuthority>> {

    override fun convert(source: Jwt): Flux<GrantedAuthority> {
        val rolesClaimValues = source.claims[ROLE_KEY_CLAIM] as ArrayList<*>
        return if (rolesClaimValues.isNotEmpty()) {
            Flux.fromIterable(
                sequenceOf(rolesClaimValues)
                    .map { "$ROLE_PREFIX$it" }
                    .map { replaceUnnecessarySymbols(it) }
                    .map { SimpleGrantedAuthority(it) }
                    .toList()
            )
        } else {
            val scopesClaimValues = source.claims[SCOPE_KEY_CLAIM] as ArrayList<*>
            Flux.fromIterable(
                sequenceOf(scopesClaimValues)
                    .map { "$SCOPE_PREFIX$it" }
                    .map { replaceUnnecessarySymbols(it) }
                    .map { SimpleGrantedAuthority(it) }
                    .toList()
            )
        }
    }

    private fun replaceUnnecessarySymbols(role: String): String {
        return role.replace("[", "")
            .replace("]", "")
    }
}
