package a.gleb.main_service.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ResumeNotFoundException(message: String? = "Not found") : ResponseStatusException(HttpStatus.NOT_FOUND, message) {
}

class ResumeConflictException(message: String? = "Conflict") : ResponseStatusException(HttpStatus.CONFLICT, message) {}