package ru.practicum.mainervice.exception.errors;

public class ForbiddenException extends ParentException {
    public ForbiddenException(String message, String reason) {
        super(message, reason);
    }
}
