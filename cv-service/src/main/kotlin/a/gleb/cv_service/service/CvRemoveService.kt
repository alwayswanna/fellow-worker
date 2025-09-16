/*
 * Copyright (c) 1-3/22/23, 8:02 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cv_service.service

import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger { }

@Service
class CvRemoveService {

    /**
     * Method which delete file from disk.
     * @param resumeMessageDeleteModel model from RabbitMQ.
     */
//    fun removeResumeFile(resumeMessageDeleteModel: ResumeMessageDelete) {
//        val fileToDelete = File("/resume/${resumeMessageDeleteModel.deleteResumeId}.pdf")
//        if (!fileToDelete.delete()) {
//            logger.info { "Can`t delete resume file with name: ${resumeMessageDeleteModel.deleteResumeId}.pdf" }
//        }
//    }
}