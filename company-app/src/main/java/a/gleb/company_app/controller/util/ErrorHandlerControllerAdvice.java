package a.gleb.company_app.controller.util;

import a.gleb.company_app.constant.ErrorCode;
import a.gleb.fellow_worker.common.ValidationErrorDto;
import a.gleb.fellow_worker.http.response.error.ErrorResponse;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

import static a.gleb.company_app.constant.CompanyAppConstant.ERROR_MESSAGE_TEMPLATE;
import static a.gleb.company_app.constant.CompanyAppConstant.TRACE_ID_RESPONSE_HEADER;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandlerControllerAdvice {

    private static final String LOG_NULL = "<null>";

    private final Tracer tracer;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        return logAndBuildErrorResponse(ErrorCode.FW_5000, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ValidationErrorDto> validationErrors = exception.getFieldErrors()
                .stream()
                .map(e -> new ValidationErrorDto(e.getField(), e.getDefaultMessage()))
                .toList();

        return logAndBuildErrorResponse(ErrorCode.FW_4002, Map.of("validationErrors", validationErrors), exception);
    }

    private ResponseEntity<ErrorResponse> logAndBuildErrorResponse(ErrorCode errorCode, Throwable throwable) {
        return logAndBuildErrorResponse(errorCode, errorCode.getMessageTemplate(), Collections.emptyMap(), throwable);
    }

    private ResponseEntity<ErrorResponse> logAndBuildErrorResponse(
            ErrorCode errorCode,
            Map<String, Object> params,
            Throwable throwable
    ) {
        return logAndBuildErrorResponse(errorCode, errorCode.getMessageTemplate(), params, throwable);
    }

    private ResponseEntity<ErrorResponse> logAndBuildErrorResponse(
            ErrorCode errorCode,
            String errorMessage,
            Map<String, Object> parameters,
            Throwable exception
    ) {
        var errorId = UUID.randomUUID().toString();
        errorMessage = Optional.ofNullable(errorMessage)
                .filter(StringUtils::isNotBlank)
                .orElse(errorCode.getMessageTemplate());

        var span = tracer.currentSpan();
        var traceId = span != null ? span.context().traceId() : LOG_NULL;

        log.error(String.format(ERROR_MESSAGE_TEMPLATE, errorId, errorCode.getCode(), errorMessage, traceId), exception);

        var rb = ResponseEntity.status(errorCode.getHttpStatusCode());
        if (span != null) {
            rb.header(TRACE_ID_RESPONSE_HEADER, traceId);
        }

        return rb.body(new ErrorResponse(LocalDateTime.now(), errorId, errorCode.getCode(), errorMessage, parameters));
    }
}
