package ru.practicum.statservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final BadRequestException e) {
        ErrorResponse errorResponse = convertToErrorResponse(HttpStatus.BAD_REQUEST, e);
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
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
