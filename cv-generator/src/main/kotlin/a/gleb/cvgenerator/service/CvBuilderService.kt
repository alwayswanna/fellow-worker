/*
 * Copyright (c) 07-1/9/23, 11:48 PM
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

            // добавляем должность
            cvDocument.addResumeTitle(messageService.getMessage("job.title.text", resumeApiModel.job))
            // добавляем имя
            cvDocument.addInfoLeftBorder(messageService.getMessage("firstname.text", resumeApiModel.firstName))
            // добавляем фамилию
            cvDocument.addInfoLeftBorder(messageService.getMessage("surname.text", resumeApiModel.middleName))
            // добавляем отчество
            if(!resumeApiModel.lastName.isNullOrEmpty()) {
                cvDocument.addInfoLeftBorder(messageService.getMessage("patronymic.text", resumeApiModel.lastName))
            }
            // добавляем дату рождения
            cvDocument.addInfoLeftBorder(messageService.getMessage("date.birth.text", resumeApiModel.birthDate))

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