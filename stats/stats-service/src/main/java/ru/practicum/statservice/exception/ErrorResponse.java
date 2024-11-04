package ru.practicum.statservice.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {

    private final List<RuntimeException> errors;

    private final String message;

    private final String reason;

    private final HttpStatus status;

    private final LocalDateTime timestamp;

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errors=" + errors +
                ", message='" + message + '\'' +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
