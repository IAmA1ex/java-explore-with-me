package ru.practicum.statservice.note;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для работы с записями запросов.
 */
@RestController
@RequiredArgsConstructor
public class NoteController {

    /** Сервис для обработки записей. */
    private final NoteService noteService;

    /**
     * Обрабатывает запрос на /hit.
     *
     * @param noteDto - объект тела запроса
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody final NoteDto noteDto) {
        noteService.hit(noteDto);
    }

    /**
     * Обрабатывает запрос на /stats.
     * Возвращает статистику по записям в заданном временном диапазоне и
     * фильтрам.
     *
     * @param start - начальная дата
     * @param end - конечная дата
     * @param uris - список URI для обработки, иначе - все
     * @param unique - считать ли одинаковые обращения к одному uri за одно
     * @return список статистики
     */
    @GetMapping("/stats")
    public List<StatDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime end,
            @RequestParam(required = false) final List<String> uris,
            @RequestParam(defaultValue = "false") final boolean unique) {
        return noteService.getStats(start, end, uris, unique);
    }

}
