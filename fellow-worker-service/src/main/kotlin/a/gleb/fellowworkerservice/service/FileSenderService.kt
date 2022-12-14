/*
 * Copyright (c) 12-12/28/22, 10:37 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks

@Service
class FileSenderService(
    private val publisher: Sinks.Many<ResumeApiModel>
) {

    /**
     * Method which send request model to RabbitMq queue for generate PDF version of resume.
     * @param request user data from web API.
     */
    suspend fun sendMessage(request: ResumeApiModel) {
        publisher.tryEmitNext(request).orThrow()
    }
}