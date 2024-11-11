package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.mainservice.exception.errors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.NOT_FOUND, e);
        logError(HttpStatus.NOT_FOUND.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ConflictException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.CONFLICT, e);
        logError(HttpStatus.CONFLICT.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, e);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final BadRequestException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, e);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final ForbiddenException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.FORBIDDEN, e);
        logError(HttpStatus.FORBIDDEN.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbidden(final MethodArgumentTypeMismatchException e) {
        ParentException parentException = new ParentException("Error converting values.", e.getMessage());
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, parentException);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbidden(final MissingServletRequestParameterException e) {
        ParentException parentException = new ParentException("Could not resolve parameter.", e.getMessage());
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, parentException);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final MethodArgumentNotValidException e) {
        Pattern pattern = Pattern.compile("\\[([^\\[\\]]*)\\]");
        Matcher matcher = pattern.matcher(e.getMessage());
        String lastPart = "";
        while (matcher.find()) {
            lastPart = matcher.group(1);
        }
        ParentException parentException = new ParentException("Validation exception.", lastPart);
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, parentException);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        String reason = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
        ParentException parentException = new ParentException("Internal server error.", reason);
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, parentException);
        logError(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse);
        return errorResponse;
    }

    private <T extends ParentException> ErrorResponse convertToErrorResponse(HttpStatus httpStatus, T exception) {
        return ErrorResponse.builder()
                .errors(List.of())
                .message(exception.getMessage())
                .reason(exception.getReason())
                .status(httpStatus)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void logError(int code, ErrorResponse errorResponse) {
        log.error("!!! ОШИБКА({}) {}.", code, errorResponse.toString());
    }
}
