package ru.practicum.statservice.exception;

public class BadRequestException extends ParentException {
    public BadRequestException(String message, String reason) {
        super(message, reason);
    }
}
