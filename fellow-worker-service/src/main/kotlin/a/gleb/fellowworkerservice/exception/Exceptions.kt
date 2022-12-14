package a.gleb.fellowworkerservice.exception

import org.springframework.http.HttpStatusCode
import org.springframework.web.server.ResponseStatusException

class UnexpectedErrorException(status: HttpStatusCode, reason: String?) : ResponseStatusException(status, reason)

class InvalidUserDataException(status: HttpStatusCode, reason: String?) : ResponseStatusException(status, reason)