/*
 * Copyright (c) 1-1/8/23, 8:41 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.models

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.imgscalr.Scalr
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import javax.imageio.ImageIO

const val PAGE_SIZE = 880

class CvDocumentModel {
    private var pdDocument: PDDocument? = null

    constructor(pdDocument: PDDocument, resumeApiModel: ResumeApiModel) {
        this.pdDocument = pdDocument
        addImageToDocument(resumeApiModel)
    }

    private fun addImageToDocument(resumeApiModel: ResumeApiModel) {
        val avatar: File = if (resumeApiModel.base64Image.isNullOrEmpty()) {
            File(
                Objects.requireNonNull(
                    javaClass.classLoader.getResource("templates/default-avatar.png").file
                )
            )
        }else{
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
        val contentStream = PDPageContentStream(
            pdDocument,
            page,
            APPEND,
            false
        )

        contentStream.drawImage(
            pdfImage,
            30f,
            (PAGE_SIZE - height * 1.5).toFloat(),
            width.toFloat(),
            height.toFloat()
        )

        contentStream.close()
    }

}
