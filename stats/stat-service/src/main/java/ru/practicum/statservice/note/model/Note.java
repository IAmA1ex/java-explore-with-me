package ru.practicum.statservice.note.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, для хранения записей запросов.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
public class Note {

    /** ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название сервиса. */
    private String app;

    /** URI. */
    private String uri;

    /** IP-адрес, с которого был сделан запрос. */
    private String ip;

    /** Время, когда был сделан запрос. */
    private LocalDateTime timestamp;

}
