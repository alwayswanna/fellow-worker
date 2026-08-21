package a.gleb.user_app.adapter.in.web.advice;

import a.gleb.user_app.domain.exception.DomainException;
import a.gleb.user_app.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        log.warn("DomainException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(toHttpStatus(ex.getErrorCode()), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        log.warn("Validation error: {}", errors);
        return problem;
    }

    /**
     * Covers unique-constraint races not caught by an application-level `existsBy...` check
     * (e.g. two concurrent requests registering the same login).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("DataIntegrityViolationException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Request conflicts with an existing resource");
    }

    /**
     * Thrown when an update targets a stale version of an optimistically-locked entity
     * (e.g. two concurrent edits of the same user or role).
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        log.warn("ObjectOptimisticLockingFailureException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "The resource was modified concurrently, please retry");
    }

    /**
     * Catch-all for anything not handled above.
     * <p>
     * {@link ErrorResponse} is an interface (not a {@link Throwable}), so it can't be used as an
     * {@code @ExceptionHandler} type directly - standard Spring MVC exceptions (bad JSON, type
     * mismatch, unsupported media type, etc.) implement it and already carry the correct status
     * and a ProblemDetail body, so those are relayed as-is. Everything else is a truly unexpected
     * exception: status stays 500, but the response avoids leaking internal details.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            log.warn("ErrorResponse: [status={}, detail={}]", errorResponse.getStatusCode(), errorResponse.getBody().getDetail());
            return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse.getBody());
        }
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"));
    }

    private HttpStatus toHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
