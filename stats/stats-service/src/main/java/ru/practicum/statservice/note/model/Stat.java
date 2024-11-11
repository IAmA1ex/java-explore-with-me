package ru.practicum.statservice.note.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для передачи статистики в ответе.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stat {

    /** Название сервиса. */
    private String app;

    /** URI. */
    private String uri;

    /** Количество запросов. */
    private Long hits;

}
