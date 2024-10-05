package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO класс для получения данных о запросе.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {

    /** Название сервиса. */
    private String app;

    /** URI. */
    private String uri;

    /** IP-адрес, с которого был сделан запрос. */
    private String ip;

    /** Время, когда был сделан запрос. */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

}
