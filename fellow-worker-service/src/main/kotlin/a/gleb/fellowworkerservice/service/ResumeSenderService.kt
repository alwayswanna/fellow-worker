/*
 * Copyright (c) 12-1/11/23, 11:29 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageBusDeleteModel
import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageBusModel
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.stereotype.Service

@Service
class ResumeSenderService(
    private val streamBridge: StreamBridge
) {

    /**
     * Method which send request model to RabbitMq queue for generate PDF version of resume.
     * @param request user data from web API.
     */
    suspend fun sendMessageCreateOrEdit(request: ResumeMessageBusModel) {
        streamBridge.send("fileSender-out-0", request)
    }

    /**
     * Method which send request model to RabbitMQ queue for delete existing resume.
     * @param request id of resume for delete.
     */
    suspend fun sendMessageRemoveFile(request: ResumeMessageBusDeleteModel){
        streamBridge.send("deleteSender-out-0", request)
    }
}