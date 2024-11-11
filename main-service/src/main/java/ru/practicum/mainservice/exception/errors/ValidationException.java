package ru.practicum.mainservice.exception.errors;

public class ValidationException extends ParentException {
    public ValidationException(String message, String reason) {
        super(message, reason);
    }
}
