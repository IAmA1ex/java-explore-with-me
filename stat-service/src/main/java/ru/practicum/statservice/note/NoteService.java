package ru.practicum.statservice.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки записей запросов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteService {

    /** Репозиторий для работы с записями. */
    private final NoteRepository noteRepository;

    /** Маппер для преобразования Note <-> NoteDto. */
    private final MapperNoteDto noteMapper;

    /**
     * Обрабатывает запись запроса.
     *
     * @param noteDto DTO объекта Note с информацией о запросе.
     */
    public void hit(final NoteDto noteDto) {
        Note note = noteMapper.toNote(noteDto);
        Note saved = noteRepository.save(note);
        log.trace("HIT: {}", saved);
    }

    /**
     * Получает статистику по запросам.
     *
     * @param start  Время начала выборки статистики.
     * @param end    Время окончания выборки статистики.
     * @param uris   Список URI для фильтрации статистики (может быть null).
     * @param unique Указывает, нужно ли группировать по уникальности адресов.
     * @return Список статистики по запросам.
     */
    public List<Stat> getStats(final LocalDateTime start,
                               final LocalDateTime end,
                               final List<String> uris,
                               final boolean unique) {
        if (unique) {
            return noteRepository.findByParamsUniqueIsTrue(start, end, uris);
        }
        return noteRepository.findByParamsUniqueIsFalse(start, end, uris);
    }
}