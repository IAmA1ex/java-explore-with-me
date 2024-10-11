package ru.practicum.mainervice.exception;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(String message) {
        super(message);
    }
}
