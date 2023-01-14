/*
 * Copyright (c) 07-1/14/23, 7:48 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import a.gleb.cvgenerator.exception.FileException
import mu.KotlinLogging
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

private val logger = KotlinLogging.logger { }

@Service
class FileDownloadService {

    /**
     * Method returns file .pdf by id.
     */
    fun download(resumeId: UUID): File {
        try {
            return File("/tmp/resume/$resumeId.pdf")
        } catch (e: Exception) {
            logger.info { "Error while download resume file with ID: $resumeId, error: $e" }
            throw FileException(HttpStatusCode.valueOf(500), "Error while get file from disk.")
        }
    }
}