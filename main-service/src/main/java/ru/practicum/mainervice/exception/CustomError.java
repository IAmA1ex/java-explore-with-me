package ru.practicum.mainervice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class CustomError extends RuntimeException {
    private final HttpStatus status;
    private final ErrorResponse errorResponse;
}
