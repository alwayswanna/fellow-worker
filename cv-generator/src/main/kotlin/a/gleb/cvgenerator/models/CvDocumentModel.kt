/*
 * Copyright (c) 1-1/9/23, 12:45 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.models

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import org.apache.pdfbox.cos.COSName.STANDARD_ENCODING
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont
import org.apache.pdfbox.pdmodel.font.encoding.Encoding
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.imgscalr.Scalr
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO


const val PAGE_SIZE = 880
const val AVATAR_TEMPLATE_DEFAULT_PATH = "templates/default-avatar.png"
const val FONT_PATH = "templates/CraftworkGroteskGX.ttf"

class CvDocumentModel(private val pdDocument: PDDocument, private val resumeApiModel: ResumeApiModel) {
    private val countHeight: Int = 20

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


    fun addInfo(height: Int?, width: Int?, message: String) {
        val page = if (countHeight < 870) {
            pdDocument.getPage(0)
        } else {
            pdDocument.addPage(PDPage())
            pdDocument.getPage(1)
        }

        val font: PDFont = PDTrueTypeFont.load(
            pdDocument,
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource(FONT_PATH)!!.file
                )
            ),
            Encoding.getInstance(STANDARD_ENCODING)
        )


        val contentStream = PDPageContentStream(pdDocument, page, APPEND, false)
        addText(contentStream, Color.BLACK, 16, 300, 250, message, font)
        contentStream.stroke()
        contentStream.close()
    }

    private fun addText(
        contentStream: PDPageContentStream,
        textColor: Color,
        fontSize: Int,
        xCoordinate: Int,
        yCoordinate: Int,
        targetText: String,
        font: PDFont
    ) {
        if (yCoordinate < 20) {
            throw IllegalStateException("Invalid y-Coordinate for write text, y: $yCoordinate")
        }

        contentStream.beginText()
        contentStream.setFont(font, fontSize.toFloat())
        contentStream.setNonStrokingColor(textColor)
        contentStream.newLineAtOffset(xCoordinate.toFloat(), yCoordinate.toFloat())
        contentStream.showText(targetText)
        contentStream.endText()
    }
}
