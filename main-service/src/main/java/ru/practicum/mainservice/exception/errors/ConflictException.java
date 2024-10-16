package ru.practicum.mainservice.exception.errors;

public class ConflictException extends ParentException {
    public ConflictException(String message, String reason) {
        super(message, reason);
    }
}
