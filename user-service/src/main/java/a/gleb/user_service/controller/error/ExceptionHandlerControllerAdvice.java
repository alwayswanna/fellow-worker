package a.gleb.user_service.controller.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import static a.gleb.user_service.constant.OAuth2ServerConstants.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(COMMA));
        log.warn("MethodArgumentNotValidException, errors, {}", message);

        return toErrorMap(request, message);
    }

    private static Map<String, String> toErrorMap(HttpServletRequest request, String message) {
        return Map.of(
                ERROR_PATH_KEY, request.getRequestURI(),
                ERROR_TIMESTAMP_KEY, LocalDateTime.now().format(FORMATTER),
                ERROR_MESSAGE_KEY, message
        );
    }
}
