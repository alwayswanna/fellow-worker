package a.gleb.main_service.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import reactor.core.publisher.Mono

data class ApiResponse<T>(
    val status: Int,
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

suspend fun <T> successResponse(data: T, message: String? = null): ResponseEntity<ApiResponse<T>> {
    return ResponseEntity.status(HttpStatus.OK)
        .body(ApiResponse(HttpStatus.OK.value(), true, data, message))
}

suspend fun <T> createdResponse(data: T, message: String? = null): ResponseEntity<ApiResponse<T>> {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse(HttpStatus.CREATED.value(), true, data, message))
}

suspend fun errorResponse(status: HttpStatus, message: String): ResponseEntity<ApiResponse<Unit>> {
    return ResponseEntity.status(status)
        .body(ApiResponse(status.value(), false, null, message))
}

fun <T> successMono(data: T, message: String? = null): Mono<ResponseEntity<ApiResponse<T>>> {
    return Mono.just(
        ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse(HttpStatus.OK.value(), true, data, message))
    )
}

fun <T> createdMono(data: T, message: String? = null): Mono<ResponseEntity<ApiResponse<T>>> {
    return Mono.just(
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(HttpStatus.CREATED.value(), true, data, message))
    )
}