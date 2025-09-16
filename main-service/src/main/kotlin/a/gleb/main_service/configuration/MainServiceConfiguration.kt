/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.main_service.configuration

import a.gleb.main_service.configuration.properties.MainServiceConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MainServiceConfigurationProperties::class)
class MainServiceConfiguration {
}