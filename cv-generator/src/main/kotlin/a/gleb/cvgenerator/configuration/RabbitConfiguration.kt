/*
 * Copyright (c) 07-1/12/23, 11:58 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.configuration

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageCreate
import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageDelete
import a.gleb.cvgenerator.service.CvBuilderService
import a.gleb.cvgenerator.service.CvRemoveService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class RabbitConfiguration {

    @Bean
    fun receiveCreate(cvBuilderService: CvBuilderService): Consumer<ResumeMessageCreate> {
        return Consumer(cvBuilderService::buildCvFile)
    }

    @Bean
    fun receiveRemove(cvRemoveService: CvRemoveService): Consumer<ResumeMessageDelete> {
        return Consumer(cvRemoveService::removeResumeFile)
    }
}