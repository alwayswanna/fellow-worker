/*
 * Copyright (c) 07-1/11/23, 11:29 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageBusModel
import a.gleb.cvgenerator.service.CvBuilderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class RabbitConfiguration {

    @Bean
    fun receiveCvData(cvBuilderService: CvBuilderService): Consumer<ResumeMessageBusModel> {
        return Consumer(cvBuilderService::buildCvFile)
    }
}