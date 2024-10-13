package ru.practicum.mainervice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.events.dao.EventRepository;
import ru.practicum.mainervice.events.dto.EventFullDto;
import ru.practicum.mainervice.events.dto.EventMapper;
import ru.practicum.mainervice.events.dto.EventShortDto;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.exception.errors.NotFoundException;
import ru.racticum.statsclient.StatsClient;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventsService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ObjectMapper objectMapper;
    private final StatsClient statsClient = new StatsClient(objectMapper);

    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Long from,
                                         Long size) {
        List<Event> events = eventRepository.findAllByPublicFilters(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                from,
                size);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> {
                    EventShortDto eventShortDto = eventMapper.toEventShortDto(e);
                    eventShortDto.setConfirmedRequests(getConfirmedRequests(e.getId()));
                    eventShortDto.setViews(getViews(e.getCreatedOn(), e.getId()));
                    return eventShortDto;
                })
                .toList();
        eventShortDtos = sortEvents(eventShortDtos, sort);

        log.debug("MAIN: {} events were found.", eventShortDtos.size());
        return eventShortDtos;
    }

    public EventFullDto getEvent(Long id) {

        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + id + " does not exist."));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(event.getId()));
        eventFullDto.setViews(getViews(event.getCreatedOn(), id));
        return eventFullDto;
    }

    private Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    private Long getViews(LocalDateTime createdOn, Long eventId) {
        return statsClient.getStats(createdOn, LocalDateTime.now(),
                List.of(String.format("/events/%d", eventId)), false).getFirst().getHits();
    }

    private List<EventShortDto> sortEvents(List<EventShortDto> events, String sort) {
        if (sort.equals("VIEWS")) {
            events.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return events;
    }
}
