package ru.practicum.statservice.note.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.statservice.note.model.Note;
import ru.practicum.statservice.note.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Поиск данных с группировкой по уникальным uri-ip.
     *
     * @param start начальная дата
     * @param end конечная дата
     * @param uris список URI
     * @return статистика
     */
    @Query(value = """
        SELECT new ru.practicum.statservice.note.model.Stat(n.app, n.uri,
        COUNT(DISTINCT n.ip))
        FROM Note n
        WHERE n.timestamp BETWEEN :start AND :end
        AND (:uris IS NULL OR n.uri IN :uris)
        GROUP BY n.app, n.uri
        ORDER BY COUNT(DISTINCT n.ip) DESC
    """)
    List<Stat> findByParamsUniqueIsTrue(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);

    /**
     * Поиск данных без группировки по уникальным uri-ip.
     *
     * @param start начальная дата
     * @param end конечная дата
     * @param uris список URI
     * @return статистика
     */
    @Query(value = """
        SELECT new ru.practicum.statservice.note.model.Stat(n.app, n.uri, COUNT(n.id))
        FROM Note n
        WHERE n.timestamp BETWEEN :start AND :end
        AND (:uris IS NULL OR n.uri IN :uris)
        GROUP BY n.app, n.uri
        ORDER BY COUNT(n.id) DESC
    """)
    List<Stat> findByParamsUniqueIsFalse(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         @Param("uris") List<String> uris);



}
