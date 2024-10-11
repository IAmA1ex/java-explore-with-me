package ru.practicum.mainervice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorResponse extends RuntimeException {

    private final List<RuntimeException> errors;

    private final String message;

    private final String reason;

    private final HttpStatus status;

    private final LocalDateTime timestamp;

}
