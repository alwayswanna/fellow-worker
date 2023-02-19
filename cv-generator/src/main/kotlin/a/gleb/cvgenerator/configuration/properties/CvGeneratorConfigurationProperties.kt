/*
 * Copyright (c) 07-2/7/23, 10:13 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.cors.CorsConfiguration

@ConfigurationProperties("cv-generator")
data class CvGeneratorConfigurationProperties(
    val securityConstraints: List<SecurityConstraints>,
    var cors: Cors
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
