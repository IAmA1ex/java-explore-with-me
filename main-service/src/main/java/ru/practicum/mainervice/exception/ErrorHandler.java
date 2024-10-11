package ru.practicum.mainervice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("Искомый объект не найден.", e.getMessage());
        logError(HttpStatus.NOT_FOUND.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedData(final DuplicatedDataException e) {
        ErrorResponse errorResponse = new ErrorResponse("Дублирование уникального поля.", e.getMessage());
        logError(HttpStatus.CONFLICT.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка валидации.", e.getMessage());
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse("Ошибка запроса.", e.getMessage());
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final ForbiddenException e) {
        ErrorResponse errorResponse = new ErrorResponse("Запрещено.", e.getMessage());
        logError(HttpStatus.FORBIDDEN.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbidden(final MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = new ErrorResponse("Unknown state: UNSUPPORTED_STATUS",
                "Проблемы при конвертации. " + e.getValue());
        logError(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final RuntimeException e) {
        ErrorResponse privateErrorResponse = new ErrorResponse("Произошла непредвиденная ошибка.",
                e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Произошла непредвиденная ошибка.",
                "Ошибка на стороне сервера.");
        logError(HttpStatus.INTERNAL_SERVER_ERROR.value(), privateErrorResponse);
        return errorResponse;
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleCustomException(final CustomError e) {
        ErrorResponse errorResponse = e.getErrorResponse();
        logError(e.getStatus().value(), errorResponse);
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    private void logError(int code, ErrorResponse errorResponse) {
        log.error("!!! ОШИБКА({}) {}.", code, errorResponse.toString());
    }
}
