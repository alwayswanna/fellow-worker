/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cv_service.configuration

import a.gleb.cv_service.configuration.properties.CvGeneratorConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CvGeneratorConfigurationProperties::class)
class CvGeneratorConfiguration {
}