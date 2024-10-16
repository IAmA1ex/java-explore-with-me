package ru.practicum.mainservice.exception.errors;

public class NotFoundException extends ParentException {
    public NotFoundException(String message, String reason) {
        super(message, reason);
    }
}
