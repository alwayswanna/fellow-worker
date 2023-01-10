/*
 * Copyright (c) 1-1/10/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.models

import a.gleb.apicommon.fellowworker.model.request.resume.EducationApiModel
import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
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

class CvDocumentModel(private val pdDocument: PDDocument, private val resumeApiModel: ResumeApiModel) {

    private val countHeight: Int = 20
    private var leftBorderStartYCoordinate = 550
    private var rightBorderStartYCoordinate = 800

    /**
     * On init this class adding image to .pdf document.
     */
    init {
        addImageToDocument(resumeApiModel)
    }

    /**
     * Method adds to resume user image.
     */
    private fun addImageToDocument(resumeApiModel: ResumeApiModel) {
        val avatar: File = if (resumeApiModel.base64Image.isNullOrEmpty()) {
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(AVATAR_TEMPLATE_DEFAULT_PATH)!!.file
                )
            )
        } else {
            val imageBytes = Base64.getDecoder().decode(resumeApiModel.base64Image)
            val file = File("avatar.${resumeApiModel.extensionPostfix}")
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

        val page: PDPage = pdDocument!!.getPage(0)
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

        contentStream.close()
    }

    /**
     * Method adds resume title to resume.
     */
    fun addResumeTitle(message: String) {
        val page = pdDocument.getPage(0)
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
        val page = if (countHeight < 870) {
            pdDocument.getPage(0)
        } else {
            pdDocument.addPage(PDPage())
            pdDocument.getPage(1)
        }

        val fondPd = PDType0Font.load(
            pdDocument,
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(FONT_PATH)!!.file
                )
            )
        )

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
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
     * Method which add by right border. CENTER to RIGHT.
     * @param message user info.
     */
    fun addInfoRightBorder(message: String) {
        val page = pdDocument.getPage(0)
        val fondPd = PDType0Font.load(
            pdDocument,
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(FONT_PATH)!!.file
                )
            )
        )

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            message,
            fondPd,
            null,
            null
        )
        contentStream.stroke()
        contentStream.close()
        rightBorderStartYCoordinate = rightBorderStartYCoordinate.minus(15)
    }

    /**
     * Add empty line by right border.
     */
    fun addSpacerByRightBorder() {
        rightBorderStartYCoordinate = rightBorderStartYCoordinate.minus(15)
    }

    fun addEducationInformation(educationApiModel: EducationApiModel) {
        val page = pdDocument.getPage(0)
        val fondPd = PDType0Font.load(
            pdDocument,
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(FONT_PATH)!!.file
                )
            )
        )

        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            educationApiModel.educationalInstitution,
            fondPd,
            null,
            null
        )

        rightBorderStartYCoordinate = rightBorderStartYCoordinate.minus(15)
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE,
            rightBorderStartYCoordinate,
            "${educationApiModel.startTime} - ${educationApiModel.endTime}",
            fondPd,
            8,
            Color.DARK_GRAY
        )
        addText(
            contentStream,
            RIGHT_BORDER_X_COORDINATE_MAX,
            rightBorderStartYCoordinate,
            educationApiModel.educationLevel.name,
            fondPd,
            8,
            Color.DARK_GRAY
        )
        contentStream.stroke()
        contentStream.close()
        rightBorderStartYCoordinate = rightBorderStartYCoordinate.minus(15)
    }

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
        contentStream.setFont(font, fontSize?.toFloat()?: 13.toFloat())
        contentStream.setNonStrokingColor(fontColor?: Color.BLACK)
        contentStream.newLineAtOffset(xCoordinate.toFloat(), yCoordinate.toFloat())
        contentStream.showText(targetText)
        contentStream.endText()
    }
}
