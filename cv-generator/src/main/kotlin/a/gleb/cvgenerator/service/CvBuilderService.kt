/*
 * Copyright (c) 07-1/9/23, 12:45 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.cvgenerator.models.CvDocumentModel
import mu.KotlinLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private val logger = KotlinLogging.logger { }

@Service
class CvBuilderService(
    private val messageService: MessageService
) {

    fun buildCvFile(resumeApiModel: ResumeApiModel) {
        logger.info { "Start create resume: ${resumeApiModel.resumeId}" }


        val template = File(
            Objects.requireNonNull(
                javaClass.classLoader.getResource("templates/template.pdf")
            ).file
        )

        var document: PDDocument? = null
        val out = ByteArrayOutputStream()
        try {
            document = PDDocument.load(template)
            val cvDocument = CvDocumentModel(document, resumeApiModel)
            cvDocument.addInfo(
                null,
                null,
                messageService.getMessage("firstname.text", resumeApiModel.firstName)
            )

            document.save(out)
            document.close()
        } catch (e: Exception) {
            throw IllegalStateException(e.message)
        } finally {
            document?.close()
        }

        File("/tmp/resume/${resumeApiModel.resumeId}.pdf").writeBytes(out.toByteArray())
    }
}