/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.cvgenerator.controller

import a.gleb.cvgenerator.service.FileDownloadService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

const val FILE_CONTROLLER_TAG = "file.download.tag.name"

@RestController
@RequestMapping("/api/v1")
@Tag(name = FILE_CONTROLLER_TAG)
class FileDownloadController(
    private val fileDownloadService: FileDownloadService
) {
    //TODO: download file by resume id.
}