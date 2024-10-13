package ru.practicum.mainervice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.categories.dao.CategoryRepository;
import ru.practicum.mainervice.categories.model.Category;
import ru.practicum.mainervice.events.dao.EventRepository;
import ru.practicum.mainervice.events.dto.EventFullDto;
import ru.practicum.mainervice.events.dto.EventMapper;
import ru.practicum.mainervice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.events.model.EventsStates;
import ru.practicum.mainervice.events.model.EventsStatesAction;
import ru.practicum.mainervice.exception.errors.BadRequestException;
import ru.practicum.mainervice.exception.errors.ConflictException;
import ru.practicum.mainervice.exception.errors.ForbiddenException;
import ru.practicum.mainervice.exception.errors.NotFoundException;
import ru.racticum.statsclient.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventsSevice {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final ObjectMapper objectMapper;
    private final StatsClient statsClient = new StatsClient(objectMapper);

    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventsStates> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Long from,
                                        Long size) {
        List<Event> events = eventRepository.findAllByAdminFilters(users,
                states.stream().map(EventsStates::toString).toList(),
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
        List<EventFullDto> eventFullDtos = events.stream().map(eventMapper::toEventFullDto).toList();
        log.info("MAIN: {} were found.", events.size());
        return eventFullDtos;
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventUpdate) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        if (!eventUpdate.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
            throw new ForbiddenException("Event date is too soon.",
                    "The event date must be at least 2 hours in the future.");

        Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(() ->
                new NotFoundException("There is no such category.",
                        "Category with id = " + eventUpdate.getCategory() + " does not exist."));

        if (eventUpdate.getAnnotation() != null) event.setAnnotation(eventUpdate.getAnnotation());
        if (eventUpdate.getCategory() != null) event.setCategory(category);
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getLocation() != null) {
            event.setLat(eventUpdate.getLocation().getLat());
            event.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) event.setPaid(eventUpdate.getPaid());
        if (eventUpdate.getParticipantLimit() != null) event.setParticipantLimit(eventUpdate.getParticipantLimit());
        if (eventUpdate.getRequestModeration() != null) event.setRequestModeration(eventUpdate.getRequestModeration());
        if (eventUpdate.getTitle() != null) event.setTitle(eventUpdate.getTitle());
        if (eventUpdate.getEventDate() != null) event.setEventDate(eventUpdate.getEventDate());

        if (eventUpdate.getStateAction() != null) {

            if (eventUpdate.getStateAction().equals(EventsStatesAction.PUBLISH_EVENT)) {

                if (!event.getState().equals(EventsStates.PENDING))
                    throw new ConflictException("Event cannot be published.",
                            "Only events in the PENDING state can be published. Current state: " + event.getState());

                if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(1)))
                    throw new ConflictException("Event cannot be published.",
                            "The event's date must be at least 1 hour in the future. Current event date: "
                                    + event.getEventDate());

                event.setState(EventsStates.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }

            if (eventUpdate.getStateAction().equals(EventsStatesAction.REJECT_EVENT)) {

                if (event.getState().equals(EventsStates.PUBLISHED))
                    throw new ConflictException("Event cannot be rejected.",
                            "Published events cannot be rejected. Current state: " + event.getState());

                event.setState(EventsStates.CANCELED);
            }
        }

        event = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(getViews(eventFullDto.getPublishedOn(), eventFullDto.getId()));

        log.debug("MAIN: {} was updated.", event);
        return eventFullDto;
    }

    private Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    private Long getViews(LocalDateTime publishedOn, Long eventId) {
        return statsClient.getStats(publishedOn, LocalDateTime.now(),
                List.of(String.format("/events/%d", eventId)), false).getFirst().getHits();
    }
}
