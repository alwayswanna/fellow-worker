/*
 * Copyright (c) 07-1/14/23, 7:48 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.controller

import a.gleb.cvgenerator.configuration.OAUTH2_SECURITY_SCHEMA
import a.gleb.cvgenerator.service.FileDownloadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType.APPLICATION_PDF
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

const val FILE_CONTROLLER_TAG = "file.download.tag.name"
const val CONTENT_DISPOSITION = "Content-Disposition"
const val DISPOSITION_HEADER_VALUE = "attachment; filename=\"resume.pdf\""

@RestController
@RequestMapping("/api/v1")
@Tag(name = FILE_CONTROLLER_TAG)
class FileDownloadController(
    private val fileDownloadService: FileDownloadService
) {

    @Operation(
        summary = "Скачать файл резюме",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)],
        tags = [FILE_CONTROLLER_TAG]
    )
    @ApiResponses(
        value = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/resume-download")
    @ResponseBody
    fun download(@RequestParam resumeId: UUID): ResponseEntity<Resource>{
        return ResponseEntity.ok()
            .contentType(APPLICATION_PDF)
            .header(CONTENT_DISPOSITION, DISPOSITION_HEADER_VALUE)
            .body(FileSystemResource(fileDownloadService.download(resumeId)))
    }
}