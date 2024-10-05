package ru.practicum.statsdto;

import lombok.*;

/**
 * Класс для передачи статистики в ответе.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {

    /** Название сервиса. */
    private String app;

    /** URI. */
    private String uri;

    /** Количество запросов. */
    private Long hits;

}
