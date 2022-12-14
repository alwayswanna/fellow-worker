/*
 * Copyright (c) 12-12/28/22, 10:37 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.configuration

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class RabbitConfiguration {

    @Bean
    fun fileSender(publisherRequestModel: Sinks.Many<ResumeApiModel>): suspend () -> Unit {
        return { publisherRequestModel }
    }

    @Bean
    fun publisherRequestModel(): Sinks.Many<ResumeApiModel> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }
}