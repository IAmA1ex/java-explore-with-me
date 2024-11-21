package ru.practicum.mainservice.events.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsGeneralFunctionality {

    private final StatsClient statsClient;

    public Long getViews(LocalDateTime createdOn, String uri, Boolean unique) {
        List<StatDto> statDtos = statsClient.getStats(createdOn, LocalDateTime.now(),
                List.of(uri), unique);
        return statDtos.isEmpty() ? 0L : statDtos.getFirst().getHits();
    }

    public boolean addView(String app, String uri, String ip) {
        NoteDto noteDto = new NoteDto(app, uri, ip, LocalDateTime.now());
        try {
            statsClient.hit(noteDto);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
