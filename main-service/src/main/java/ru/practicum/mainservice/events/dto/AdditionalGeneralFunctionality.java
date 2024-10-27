package ru.practicum.mainservice.events.dto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
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
public class AdditionalGeneralFunctionality {

    private final EventRepository eventRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Setter
    private StatsClient statsClient;

    @Value("${host}")
    private String host;

    @PostConstruct
    public void init() {
        statsClient = new StatsClient(host);
    }

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
