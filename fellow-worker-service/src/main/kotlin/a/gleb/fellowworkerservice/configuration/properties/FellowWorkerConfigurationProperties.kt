/*
 * Copyright (c) 12/26/22, 11:56 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("fellow-worker")
data class FellowWorkerConfigurationProperties(
    val securityConstraints: List<SecurityConstraints>,
    val unprotectedPaths: List<String>
)

data class SecurityConstraints(
    val securityCollections: List<SecurityCollection>,
    val roles: List<String>
)

data class SecurityCollection(
    val patterns: List<String>,
    val methods: List<String>?
)
