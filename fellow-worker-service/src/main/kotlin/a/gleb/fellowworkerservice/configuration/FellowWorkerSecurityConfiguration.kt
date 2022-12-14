/*
 * Copyright (c) 12/26/22, 11:56 PM.
 * Created by https://github.com/alwayswanna
 *
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
import reactor.core.publisher.Flux

val UNAUTHORIZED_PATTERNS = listOf(
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
            .cors().and().csrf().disable()
            .authorizeExchange { restrictApiMethods(it) }
            .oauth2ResourceServer()
            .jwt {
                var reactiveJwtConverter = ReactiveJwtAuthenticationConverter()
                reactiveJwtConverter.setJwtGrantedAuthoritiesConverter(SpringBootReactiveJwtConverter())

                it.jwtAuthenticationConverter(reactiveJwtConverter)
            }

        return httpSecurity.build()
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

        val unprotectedPaths = properties.unprotectedPaths
        val permitAllMappings = (unprotectedPaths + UNAUTHORIZED_PATTERNS).toTypedArray()

        authorizeExchangeSpec.pathMatchers(*permitAllMappings).permitAll()
    }
}

class SpringBootReactiveJwtConverter : Converter<Jwt, Flux<GrantedAuthority>> {

    private val rolePrefix: String = "ROLE_"
    private val roleKeyClaim: String = "role"

    override fun convert(source: Jwt): Flux<GrantedAuthority> {
        return Flux.fromIterable(
            sequenceOf(
                replaceUnnecessarySymbols(source.claims[roleKeyClaim].toString()).split(",")
            )
                .map { "$rolePrefix$it" }
                .map { replaceUnnecessarySymbols(it) }
                .map { SimpleGrantedAuthority(it) }
                .toList()
        )
    }

    private fun replaceUnnecessarySymbols(role: String): String {
        return role.replace("[", "")
            .replace("]", "")
    }
}
