/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cv-generator")
data class CvGeneratorConfigurationProperties(
    val securityConstraints: List<SecurityConstraints>,
    val unprotectedPaths: List<String>?
)

data class SecurityConstraints(
    val securityCollections: List<SecurityCollection>,
    val roles: List<String>
)

data class SecurityCollection(
    val patterns: List<String>,
    val methods: List<String>?
)
