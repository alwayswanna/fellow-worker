package a.gleb.main_service.controller.advice

import a.gleb.main_service.constant.DEFAULT_ERROR_MESSAGE
import a.gleb.main_service.constant.DEFAULT_STATUS_CODE
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class MainServiceRestControllerAdvice {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException, exchange: ServerWebExchange):
            ResponseEntity<Map<String, String?>> {
        logger.debug(
            "Handle exception, name={}, status={}, message={}",
            e::class.java.simpleName,
            e.statusCode,
            e.reason
        )

        return ResponseEntity
            .status(e.statusCode)
            .body(buildErrorParams(e.reason, exchange, e.statusCode.value()))
    }

    /**
     * Build map for response.
     */
    private fun buildErrorParams(
        message: String?,
        exchange: ServerWebExchange,
        statusCode: Int?
    ): Map<String, String?> {
        val status = statusCode?.toString() ?: DEFAULT_STATUS_CODE.toString()
        val msg = message ?: DEFAULT_ERROR_MESSAGE

        return mapOf(
            "code" to status,
            "path" to exchange.request.path.value(),
            "message" to msg,
            "timestamp" to LocalDateTime.now().toString(),
        )
    }
}