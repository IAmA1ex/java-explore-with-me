package ru.practicum.mainservice.events.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsGeneralFunctionality {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String dateToString(LocalDateTime localDateTime) {
        return dateTimeFormatter.format(localDateTime);
    }

    public Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    public Long getViews(LocalDateTime createdOn, String uri, Boolean unique) {
        List<StatDto> statDtos = statsClient.getStats(createdOn, LocalDateTime.now(),
                List.of(uri), unique);
        return statDtos.isEmpty() ? 0L : statDtos.getFirst().getHits();
    }

    /*public List<StatDto> getStats(LocalDateTime start, LocalDateTime end,
                List<String> uris, boolean unique) {
        List<StatDto> statDtos = statsClient.getStats(start, end,
                uris, false);
        return statDtos;
    }*/

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
