/*
 * Copyright (c) 12-1/26/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.cors.CorsConfiguration

@ConfigurationProperties("fellow-worker")
data class FellowWorkerConfigurationProperties(
    val securityConstraints: List<SecurityConstraints>,
    val unprotectedPaths: List<String>,
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