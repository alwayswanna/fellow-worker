/*
 * Copyright (c) 07-1/10/23, 11:08 PM
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
            if (!resumeApiModel.lastName.isNullOrEmpty()) {
                cvDocument.addInfoLeftBorder(messageService.getMessage("patronymic.text", resumeApiModel.lastName))
            }
            // добавляем дату рождения
            cvDocument.addInfoLeftBorder(messageService.getMessage("date.birth.text", resumeApiModel.birthDate))
            // добавляем дату о себе
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(messageService.getMessage("about.text"))
            val listAbout = resumeApiModel.about.split(" ").asSequence().chunked(4).toList()
            listAbout.forEach {
                cvDocument.addInfoLeftBorder(it.joinToString(separator = " "))
            }
            // добавляем ожидаемую зарплату
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(
                messageService.getMessage(
                    "expected.salary.text",
                    resumeApiModel.expectedSalary
                )
            )
            // добавляем основные навыки
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(messageService.getMessage("main.skills.text"))
            resumeApiModel.professionalSkills.forEach {
                cvDocument.addInfoLeftBorder(" - $it")
            }
            // добавляем контактную информацию
            cvDocument.addInfoRightBorder(messageService.getMessage("contact.info.text"))
            cvDocument.addInfoRightBorder(messageService.getMessage("phone.number.text", resumeApiModel.contact.phone))
            cvDocument.addInfoRightBorder(messageService.getMessage("email.text", resumeApiModel.contact.email))

            // добавляем данные об образовании
            cvDocument.addSpacerByRightBorder()
            cvDocument.addInfoRightBorder(messageService.getMessage("education.label.text"))
            resumeApiModel.education.forEach {
                cvDocument.addEducationInformation(it)
            }



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