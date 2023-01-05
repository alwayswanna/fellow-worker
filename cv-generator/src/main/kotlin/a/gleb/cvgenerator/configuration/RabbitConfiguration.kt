/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.cvgenerator.service.CvBuilderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class RabbitConfiguration {

    @Bean
    fun receiveCvData(cvBuilderService: CvBuilderService): Consumer<ResumeApiModel> {
        return Consumer(cvBuilderService::buildCvFile)
    }
}