/*
 * Copyright (c) 07-2/7/23, 10:13 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration

import a.gleb.cvgenerator.configuration.properties.CvGeneratorConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

const val ROLE_PREFIX = "ROLE_"
const val ROLE_KEY_CLAIM = "role"

private val unauthorizedPatterns: List<String> = listOf(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/actuator/**",
    "/error"
)

@Configuration
@EnableWebSecurity
class CvGeneratorSecurityConfiguration(
    private val properties: CvGeneratorConfigurationProperties
) {

    /**
     * Configure Spring security for microservice.
     */
    @Bean
    fun oauth2SecurityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().cors()
            .and()
            .csrf()
            .disable()
            .authorizeHttpRequests(this::configure)
            .oauth2ResourceServer()
            .jwt {
                val converter = JwtAuthenticationConverter()
                converter.setJwtGrantedAuthoritiesConverter(SpringBootOAuth2Converter())
                it.jwtAuthenticationConverter(converter)
            }

        return httpSecurity.build()
    }

    /**
     * Method generate CORS for microservice.
     */
    @Bean
    fun corsConfigurationSource(properties: CvGeneratorConfigurationProperties): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", properties.cors)
        return source
    }

    /**
     * Method which restrict rest-api methods by roles from config.
     */
    private fun configure(
        registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
    ) {
        properties.securityConstraints.forEach {
            val roles = it.roles.toTypedArray()
            it.securityCollections.forEach { securityCollection ->
                val patterns = securityCollection.patterns.toTypedArray()
                securityCollection.methods?.forEach { method ->
                    registry.requestMatchers(HttpMethod.valueOf(method), *patterns).hasAnyRole(*roles)
                } ?: registry.requestMatchers(*patterns).hasAnyRole(*roles)
            }
        }

        registry.requestMatchers(*unauthorizedPatterns.toTypedArray()).permitAll()
    }

    /**
     * Class converter for convert roles from JWT token to [SimpleGrantedAuthority]
     */
    private class SpringBootOAuth2Converter : Converter<Jwt?, Collection<GrantedAuthority?>?> {

        override fun convert(source: Jwt): Collection<GrantedAuthority?>? {
            return sequenceOf(
                replaceUnnecessarySymbols(source.claims[ROLE_KEY_CLAIM].toString()).split(",")
            )
                .map { "$ROLE_PREFIX$it" }
                .map { replaceUnnecessarySymbols(it) }
                .map { SimpleGrantedAuthority(it) }
                .toList()
        }

        /* method which remove useless symbols from role */
        private fun replaceUnnecessarySymbols(role: String): String {
            return role.replace("[", "")
                .replace("]", "")
        }
    }
}