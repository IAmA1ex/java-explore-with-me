package ru.practicum.mainervice.exception.errors;

public class ConflictException extends ParentException {
    public ConflictException(String message, String reason) {
        super(message, reason);
    }
}
