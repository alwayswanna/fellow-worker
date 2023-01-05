/*
 * Copyright (c) 12-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

@Service
class FileSenderService(
    private val streamBridge: StreamBridge
) {

    /**
     * Method which send request model to RabbitMq queue for generate PDF version of resume.
     * @param request user data from web API.
     */
    suspend fun sendMessage(request: ResumeApiModel) {
        streamBridge.send("fileSender-out-0", request)
    }
}