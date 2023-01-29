/*
 * Copyright (c) 1-2/15/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.models

import a.gleb.apicommon.fellowworker.model.response.resume.EducationResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.EducationResponseModel.EducationLevel.BACHELOR
import a.gleb.apicommon.fellowworker.model.response.resume.EducationResponseModel.EducationLevel.SPECIALTY
import a.gleb.apicommon.fellowworker.model.response.resume.WorkExperienceResponseModel
import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageCreate
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.imgscalr.Scalr
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO


const val PAGE_SIZE = 880
const val LEFT_BORDER_X_COORDINATE = 20
const val RIGHT_BORDER_X_COORDINATE = 260
const val RIGHT_BORDER_X_COORDINATE_MAX = 400

/* DEFAULT AVATAR IF IMAGE FROM REQUEST IS NULL */
const val AVATAR_TEMPLATE_DEFAULT_PATH = "templates/default-avatar.png"

/* PATH TO FONTS */
const val BOLD_FONT_PATH = "templates/Nunito-Bold.ttf"
const val FONT_PATH = "templates/Nunito-Regular.ttf"

/* RU education levels */
const val spec = "Специалитет"
const val bac = "Бакалавриат"
const val mag = "Магистрант"

fun determineEduLevelOfResume(educationLevel: EducationResponseModel.EducationLevel): String {
    return if (BACHELOR == educationLevel) {
        bac
    } else if (SPECIALTY == educationLevel) {
        spec
    } else {
        mag
    }
}

class CvDocumentModel(
    private val pdDocument: PDDocument,
    private val resumeResponseModel: ResumeMessageCreate,
    private val pdFont: PDFont

) {

    private var pageIndex: Int = 0
    private var leftBorderStartYCoordinate = 550
    private var rightBorderStartYCoordinate = 800

    /**
     * On init this class adding image to .pdf document.
     */
    init {
        addImageToDocument(resumeResponseModel)
    }

    /**
     * Method adds to resume user image.
     * @param resumeResponseModel data from request.
     */
    private fun addImageToDocument(resumeResponseModel: ResumeMessageCreate) {
        val avatar: File = if (resumeResponseModel.base64Image.isNullOrEmpty()) {
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(AVATAR_TEMPLATE_DEFAULT_PATH)!!.file
                )
            )
        } else {
            val imageBytes = Base64.getDecoder().decode(resumeResponseModel.base64Image)
            val file = File("avatar.${resumeResponseModel.imageExtension}")
            file.writeBytes(imageBytes)
            file
        }

        val image = Scalr.resize(
            ImageIO.read(avatar.inputStream()),
            Scalr.Method.QUALITY,
            Scalr.Mode.AUTOMATIC,
            2500,
            Scalr.OP_ANTIALIAS
        )

        val byteArrayOutStream = ByteArrayOutputStream()
        ImageIO.write(image, avatar.extension, byteArrayOutStream)

        val pdfImage = PDImageXObject.createFromByteArray(
            pdDocument,
            byteArrayOutStream.toByteArray(),
            avatar.nameWithoutExtension
        )

        val page: PDPage = pdDocument!!.getPage(pageIndex)
        val height: Int = image.height / 12
        val width: Int = image.width / 12
        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)

        contentStream.drawImage(
            pdfImage,
            30f,
            (PAGE_SIZE - height * 1.5).toFloat(),
            width.toFloat(),
            height.toFloat()
        )

        /* remove file and close stream of pdf file */
        Files.delete(avatar.toPath())
        contentStream.close()
    }

    /**
     * Method adds resume title to resume.
     * @param message data from request, title of resume.
     */
    fun addResumeTitle(message: String) {
        val page = pdDocument.getPage(pageIndex)
        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        val fondPd = PDType0Font.load(
            pdDocument,
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(BOLD_FONT_PATH)!!.file
                )
            )
        )

        addText(
            contentStream,
            LEFT_BORDER_X_COORDINATE,
            leftBorderStartYCoordinate,
            message,
            fondPd,
            null,
            null
        )

        contentStream.stroke()
        contentStream.close()
        leftBorderStartYCoordinate = leftBorderStartYCoordinate.minus(15)
    }

    /**
     * Add empty line by left border.
     */
    fun addSpacerByLeftBorder() {
        leftBorderStartYCoordinate = leftBorderStartYCoordinate.minus(15)
    }

    /**
     * Method which add user info by left border. LEFT to CENTER.
     * @param message user info.
     */
    fun addInfoLeftBorder(message: String) {
        val page = pdDocument.getPage(pageIndex)

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            LEFT_BORDER_X_COORDINATE,
            leftBorderStartYCoordinate,
            message,
            pdFont,
            null,
            null
        )
        contentStream.stroke()
        contentStream.close()
        leftBorderStartYCoordinate = leftBorderStartYCoordinate.minus(15)
    }

    /**
     * Method which add by right border. CENTER to RIGHT.
     * @param message user info.
     */
    fun addInfoRightBorder(message: String) {
        val page = pdDocument.getPage(pageIndex)

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            message,
            pdFont,
            null,
            null
        )
        contentStream.stroke()
        contentStream.close()
        rightBorderStartYCoordinate -= 15
    }

    /**
     * Add empty line by right border.
     */
    fun addSpacerByRightBorder() {
        rightBorderStartYCoordinate -= 15
    }

    /**
     * Method ads education data information, on resume .pdf.
     * @param educationApiModel data from request.
     */
    fun addEducationInformation(educationApiModel: EducationResponseModel) {
        val page = pdDocument.getPage(pageIndex)

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            educationApiModel.educationalInstitution,
            pdFont,
            null,
            null
        )

        rightBorderStartYCoordinate -= 15
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            "${educationApiModel.startTime} - ${educationApiModel.endTime}",
            pdFont,
            8,
            Color.DARK_GRAY
        )
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE_MAX,
            rightBorderStartYCoordinate,
            determineEduLevelOfResume(educationApiModel.educationLevel),
            pdFont,
            8,
            Color.DARK_GRAY
        )
        contentStream.stroke()
        contentStream.close()
        rightBorderStartYCoordinate -= 15
    }

    /**
     * Method ads work experience, on resume .pdf.
     * @param workExperienceApiModel data from request.
     */
    fun addWorkExperienceInformation(workExperienceApiModel: WorkExperienceResponseModel) {
        val page = if (rightBorderStartYCoordinate > 35) {
            pdDocument.getPage(pageIndex)
        } else {
            pageIndex = pageIndex.plus(1)
            pdDocument.addPage(PDPage())
            pdDocument.getPage(pageIndex)
        }

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            workExperienceApiModel.companyName,
            pdFont,
            null,
            null
        )

        rightBorderStartYCoordinate -= 15
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            "${workExperienceApiModel.startTime} - ${workExperienceApiModel.endTime}",
            pdFont,
            8,
            Color.DARK_GRAY
        )
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE_MAX,
            rightBorderStartYCoordinate,
            workExperienceApiModel.workingSpeciality,
            pdFont,
            8,
            Color.DARK_GRAY
        )
        rightBorderStartYCoordinate -= 15
        workExperienceApiModel.responsibilities.split(" ").asSequence().chunked(4).forEach {
            addText(
                contentStream,
                RIGHT_BORDER_X_COORDINATE,
                rightBorderStartYCoordinate,
                it.joinToString(separator = " "),
                pdFont,
                8,
                Color.DARK_GRAY
            )
            rightBorderStartYCoordinate -= 15
        }

        contentStream.stroke()
        contentStream.close()
        rightBorderStartYCoordinate -= 15
    }

    /**
     * Method ads test to .pdf file.
     * @param contentStream stream bytes of document.
     * @param xCoordinate coordinate by 0x.
     * @param yCoordinate coordinate by 0y.
     * @param targetText data for add.
     * @param font font type.
     * @param fontSize text size.
     * @param fontColor text color.
     */
    private fun addText(
        contentStream: PDPageContentStream,
        xCoordinate: Int,
        yCoordinate: Int,
        targetText: String,
        font: PDFont,
        fontSize: Int?,
        fontColor: Color?
    ) {
        if (yCoordinate < 20) {
            throw IllegalStateException("Invalid y-Coordinate for write text, y: $yCoordinate")
        }

        contentStream.beginText()
        contentStream.setFont(font, fontSize?.toFloat() ?: 13.toFloat())
        contentStream.setNonStrokingColor(fontColor ?: Color.BLACK)
        contentStream.newLineAtOffset(xCoordinate.toFloat(), yCoordinate.toFloat())
        contentStream.showText(targetText)
        contentStream.endText()
    }
}
