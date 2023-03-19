/*
 * Copyright (c) 3-3/25/23, 8:21 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.controller

import a.gleb.fellowworkerservice.configuration.OAUTH2_SECURITY_SCHEMA
import a.gleb.fellowworkerservice.service.SupportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*


const val SUPPORT_CONTROLLER_TAG = "support.tag.name"

@RestController
@RequestMapping("/api/v1/support")
@Tag(name = SUPPORT_CONTROLLER_TAG)
class SupportController(
    private val supportService: SupportService
) {

    @Operation(
        summary = "Удалить все данные связанные с пользователем",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/remove")
    suspend fun remove(@RequestParam id: UUID) {
        supportService.removeUserEntities(id)
    }
}