/*
 * Copyright (c) 07-1/11/23, 11:29 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageBusModel
import a.gleb.cvgenerator.models.CvDocumentModel
import a.gleb.cvgenerator.models.FONT_PATH
import mu.KotlinLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

private val logger = KotlinLogging.logger { }

@Service
class CvBuilderService(
    private val messageService: MessageService
) {

    fun buildCvFile(resumeResponseModel: ResumeMessageBusModel) {
        logger.info { "Start create resume: ${resumeResponseModel.resume.resumeId}" }
        val resume = resumeResponseModel.resume


        val template = File(
            Objects.requireNonNull(
                javaClass.classLoader.getResource("templates/template.pdf")
            ).file
        )

        var document: PDDocument? = null
        val out = ByteArrayOutputStream()
        try {
            document = PDDocument.load(template)
            val pdFont = PDType0Font.load(
                document,
                File(
                    Objects.requireNonNull(
                        javaClass.classLoader.getResource(FONT_PATH)!!.file
                    )
                )
            )
            val cvDocument = CvDocumentModel(document, resumeResponseModel, pdFont)

            // добавляем должность
            cvDocument.addResumeTitle(messageService.getMessage("job.title.text", resume.job))
            // добавляем имя
            cvDocument.addInfoLeftBorder(messageService.getMessage("firstname.text", resume.firstName))
            // добавляем фамилию
            cvDocument.addInfoLeftBorder(messageService.getMessage("surname.text", resume.middleName))
            // добавляем отчество
            if (!resume.lastName.isNullOrEmpty()) {
                cvDocument.addInfoLeftBorder(messageService.getMessage("patronymic.text", resume.lastName))
            }
            // добавляем дату рождения
            cvDocument.addInfoLeftBorder(messageService.getMessage("date.birth.text", resume.birthDate))
            // добавляем дату о себе
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(messageService.getMessage("about.text"))
            val listAbout = resume.about.split(" ").asSequence().chunked(4).toList()
            listAbout.forEach {
                cvDocument.addInfoLeftBorder(it.joinToString(separator = " "))
            }

            // добавляем ожидаемую зарплату
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(
                messageService.getMessage(
                    "expected.salary.text",
                    resume.expectedSalary
                )
            )

            // добавляем основные навыки
            cvDocument.addSpacerByLeftBorder()
            cvDocument.addInfoLeftBorder(messageService.getMessage("main.skills.text"))
            resume.professionalSkills.forEach {
                cvDocument.addInfoLeftBorder(" - $it")
            }

            // добавляем контактную информацию
            cvDocument.addInfoRightBorder(messageService.getMessage("contact.info.text"))
            cvDocument.addInfoRightBorder(messageService.getMessage("phone.number.text", resume.contact.phone))
            cvDocument.addInfoRightBorder(messageService.getMessage("email.text", resume.contact.email))

            // добавляем данные об образовании
            cvDocument.addSpacerByRightBorder()
            cvDocument.addInfoRightBorder(messageService.getMessage("education.label.text"))
            resume.education.forEach {
                cvDocument.addEducationInformation(it)
            }

            // добавляем данные об опыте работы
            cvDocument.addSpacerByRightBorder()
            cvDocument.addInfoRightBorder(messageService.getMessage("work.experience.title.text"))
            resume.workingHistory.forEach {
                cvDocument.addWorkExperienceInformation(it)
            }

            document.save(out)
            document.close()
        } catch (e: Exception) {
            throw IllegalStateException(e.message)
        } finally {
            document?.close()
        }

        File("/tmp/resume/${resumeResponseModel.resume.resumeId}.pdf").writeBytes(out.toByteArray())
    }
}