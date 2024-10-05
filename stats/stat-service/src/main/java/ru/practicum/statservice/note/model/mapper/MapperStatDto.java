package ru.practicum.statservice.note.model.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.statsdto.StatDto;
import ru.practicum.statservice.note.model.Stat;

@Component
public class MapperStatDto {

    public StatDto toStatDto(final Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(stat.getHits())
                .build();
    }

    public Stat toStat(final StatDto statDto) {
        return Stat.builder()
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .hits(statDto.getHits())
                .build();
    }

}
