package ru.practicum.mainervice.exception.errors;

import lombok.Getter;

@Getter
public class ParentException extends RuntimeException {

    private final String reason;

    public ParentException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
