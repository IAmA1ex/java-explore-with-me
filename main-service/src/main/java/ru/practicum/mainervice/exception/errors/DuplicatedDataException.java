package ru.practicum.mainervice.exception.errors;

public class DuplicatedDataException extends ParentException {
    public DuplicatedDataException(String message, String reason) {
        super(message, reason);
    }
}
