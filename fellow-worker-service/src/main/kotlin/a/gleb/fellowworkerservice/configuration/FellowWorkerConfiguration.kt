/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.configuration

import a.gleb.fellowworkerservice.configuration.properties.FellowWorkerConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(FellowWorkerConfigurationProperties::class)
class FellowWorkerConfiguration {
}