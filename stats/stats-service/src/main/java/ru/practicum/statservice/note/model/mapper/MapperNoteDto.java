package ru.practicum.statservice.note.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statservice.note.model.Note;

/**
 * Mapper для преобразования Note <-> NoteDto.
 */
@Component
public class MapperNoteDto {

    /**
     * Преобразует Note -> NoteDto.
     *
     * @param note объект Note, который нужно преобразовать
     * @return объект NoteDto, созданный из объекта Note
     */
    public NoteDto toNoteDto(final Note note) {
        return NoteDto.builder()
                .app(note.getApp())
                .ip(note.getIp())
                .uri(note.getUri())
                .timestamp(note.getTimestamp())
                .build();
    }

    /**
     * Преобразует NoteDto -> Note.
     *
     * @param noteDto объект NoteDto, который нужно преобразовать
     * @return объект Note, созданный из объекта NoteDto
     */
    public Note toNote(final NoteDto noteDto) {
        return Note.builder()
                .app(noteDto.getApp())
                .ip(noteDto.getIp())
                .uri(noteDto.getUri())
                .timestamp(noteDto.getTimestamp())
                .build();
    }

}
