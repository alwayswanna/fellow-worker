/*
 * Copyright (c) 12-3/25/23, 8:21 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.main_service.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.cors.CorsConfiguration

@ConfigurationProperties("main-service")
data class MainServiceConfigurationProperties(

    /**
     * Method which closed with JWT `roles`
     */
    val securityConstraints: List<SecurityConstraints>,

    /**
     * Unprotected REST methods
     */
    val unprotectedPaths: List<String>,

    /**
     * Methods witch closed with JWT `scope`
     */
    val scopedPaths: List<String>,

    /**
     * CORS configuration
     */
    val cors: Cors
)

data class SecurityConstraints(
    val securityCollections: List<SecurityCollection>,
    val roles: List<String>
)

data class SecurityCollection(
    val patterns: List<String>,
    val methods: List<String>?
)

class Cors : CorsConfiguration()