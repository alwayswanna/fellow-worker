package a.gleb.user_app.controller.util;

import a.gleb.user_app.exception.UserAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(UserAppException.class)
    public ProblemDetail handleUserAppException(UserAppException ex) {
        log.warn("UserAppException: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(ex.getStatusCode()), ex.getMessage());
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
}
