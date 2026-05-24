package a.gleb.resume_app.controller.util;

import a.gleb.fellow_worker.common.ValidationErrorDto;
import a.gleb.fellow_worker.http.response.error.ErrorResponse;
import a.gleb.resume_app.constant.ErrorCode;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.*;

import static a.gleb.resume_app.constant.ResumeAppConstant.ERROR_MESSAGE_TEMPLATE;
import static a.gleb.resume_app.constant.ResumeAppConstant.TRACE_ID_RESPONSE_HEADER;

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
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return logAndBuildErrorResponse(
                ErrorCode.FW_4002,
                Map.of("validationErrors", validationErrors),
                exception);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> missingServletRequestPartException(MissingServletRequestPartException ex) {
        return this.logAndBuildErrorResponse(ErrorCode.FW_4002, ex.getMessage(), Map.of(), ex);
    }

    private ResponseEntity<ErrorResponse> logAndBuildErrorResponse(ErrorCode errorCode, Throwable throwable) {
        return this.logAndBuildErrorResponse(errorCode, errorCode.getMessageTemplate(), Collections.emptyMap(), throwable);
    }

    private ResponseEntity<ErrorResponse> logAndBuildErrorResponse(
            ErrorCode errorCode,
            Map<String, Object> params,
            Throwable throwable
    ) {
        return this.logAndBuildErrorResponse(errorCode, errorCode.getMessageTemplate(), params, throwable);
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
        var traceID = span != null ? span.context().traceId() : LOG_NULL;

        log.error(
                String.format(
                        ERROR_MESSAGE_TEMPLATE,
                        errorId,
                        errorCode.getCode(),
                        errorMessage,
                        traceID
                ),
                exception
        );

        var rb = ResponseEntity.status(errorCode.getHttpStatusCode());
        if (span != null) {
            rb.header(TRACE_ID_RESPONSE_HEADER, traceID);
        }

        return rb.body(new ErrorResponse(LocalDateTime.now(), errorId, errorCode.getCode(), errorMessage, parameters));
    }

}
