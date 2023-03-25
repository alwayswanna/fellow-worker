/*
 * Copyright (c) 07-3/22/23, 10:57 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.service

import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageCreate
import a.gleb.cvgenerator.models.CvDocumentModel
import a.gleb.cvgenerator.models.FONT_PATH
import mu.KotlinLogging
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File

private val logger = KotlinLogging.logger { }

@Service
class CvBuilderService(
    private val messageService: MessageService
) {

    fun buildCvFile(resumeResponseModel: ResumeMessageCreate) {
        logger.info { "Start create resume: ${resumeResponseModel.resume.resumeId}" }
        val resume = resumeResponseModel.resume

        val template: File = extractFile(
            File.createTempFile("template", ".pdf"),
            "templates/template.pdf"
        )

        var document: PDDocument? = null
        val out = ByteArrayOutputStream()
        try {
            document = PDDocument.load(template)

            val fontFile: File = extractFile(File.createTempFile("Nunito-Regular", ".ttf"), FONT_PATH)
            val pdFont = PDType0Font.load(document, fontFile)
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

            if (resume.education != null && resume.education.isNotEmpty()){
                resume.education.forEach {
                    cvDocument.addEducationInformation(it)
                }
            }

            // добавляем данные об опыте работы
            cvDocument.addSpacerByRightBorder()
            cvDocument.addInfoRightBorder(messageService.getMessage("work.experience.title.text"))

            if (resume.workingHistory !== null && resume.workingHistory.isNotEmpty()) {
                resume.workingHistory.forEach {
                    cvDocument.addWorkExperienceInformation(it)
                }
            }

            document.save(out)
            document.close()
        } catch (e: Exception) {
            throw IllegalStateException(e.message)
        } finally {
            document?.close()
        }

        File("/resume/${resumeResponseModel.resume.resumeId}.pdf").writeBytes(out.toByteArray())
    }

    private fun extractFile(file: File, pathToFile: String): File {
        logger.info { "Extract resource file with path: $pathToFile" }
        val inputStreamFont = javaClass.classLoader.getResourceAsStream(pathToFile)
        inputStreamFont.use { input ->
            file.outputStream().use { output ->
                input!!.copyTo(output)
            }
        }

        return file
    }
}