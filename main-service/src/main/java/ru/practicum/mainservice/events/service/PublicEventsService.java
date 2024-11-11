package ru.practicum.mainservice.events.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.StatsGeneralFunctionality;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.EventShortDto;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.NotFoundException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventsService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatsGeneralFunctionality agf;

    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Long from,
                                         Long size, HttpServletRequest request) {
        if (categories != null && categories.stream().anyMatch(categoryId -> categoryId < 1)) {
            throw new BadRequestException("Invalid category ID.",
                    "All category IDs must be greater than or equal to 1.");
        }

        if (categories != null && categoryRepository.findAllById(categories).size() != categories.size())
            throw new NotFoundException("One or more categories not found", "Some categories do not exist");

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
                    eventShortDto.setConfirmedRequests(agf.getConfirmedRequests(e.getId()));
                    eventShortDto.setViews(agf.getViews(e.getCreatedOn(), "/events/" + e.getId(), true));
                    return eventShortDto;
                })
                .toList();
        eventShortDtos = sortEvents(eventShortDtos, sort);

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        agf.addView("explore-with-me", uri, ip);

        log.debug("MAIN: {} events were found.", eventShortDtos.size());
        return eventShortDtos;
    }

    public EventFullDto getEvent(Long id, HttpServletRequest request) {

        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + id + " does not exist."));

        if (event.getPublishedOn() == null)
            throw new NotFoundException("Event not published.",
                    "Event with id = " + id + " has not been published yet.");

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        agf.addView("explore-with-me", uri, ip);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(agf.getConfirmedRequests(event.getId()));
        eventFullDto.setViews(agf.getViews(event.getCreatedOn(), "/events/" + event.getId(), true));
        return eventFullDto;
    }

    private List<EventShortDto> sortEvents(List<EventShortDto> events, String sort) {
        if (sort.equals("VIEWS")) {
            return events.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        return events;
    }
}
