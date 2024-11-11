package ru.practicum.mainservice.exception.errors;

public class BadRequestException extends ParentException {
    public BadRequestException(String message, String reason) {
        super(message, reason);
    }
}
