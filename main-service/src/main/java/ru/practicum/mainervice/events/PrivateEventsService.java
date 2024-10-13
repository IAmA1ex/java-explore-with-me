package ru.practicum.mainervice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.categories.dao.CategoryRepository;
import ru.practicum.mainervice.categories.model.Category;
import ru.practicum.mainervice.events.dao.EventRepository;
import ru.practicum.mainervice.events.dto.*;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.events.model.EventRequestStatus;
import ru.practicum.mainervice.events.model.EventsStates;
import ru.practicum.mainervice.events.model.EventsStatesAction;
import ru.practicum.mainervice.exception.errors.BadRequestException;
import ru.practicum.mainervice.exception.errors.ForbiddenException;
import ru.practicum.mainervice.exception.errors.NotFoundException;
import ru.practicum.mainervice.participants.dao.ParticipationRepository;
import ru.practicum.mainervice.participants.dto.ParticipationMapper;
import ru.practicum.mainervice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainervice.participants.model.Participant;
import ru.practicum.mainervice.user.dao.UserRepository;
import ru.practicum.mainervice.user.model.User;
import ru.racticum.statsclient.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateEventsService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRepository participationRepository;
    private final EventMapper eventMapper;
    private final ObjectMapper objectMapper;
    private final ParticipationMapper participationMapper;
    private final StatsClient statsClient = new StatsClient(objectMapper);

    public List<EventShortDto> getEventsCreatedByUser(Long userId, Integer from, Integer size) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        List<Event> events;
        if (from == null || size == null) events = eventRepository.findAllByInitiatorId(userId);
        else events = eventRepository.findAllByInitiatorIdSorted(userId, from, size);

        List<EventShortDto> eventShortDtos = events.stream().map(e -> {
            EventShortDto eventShortDto = eventMapper.toEventShortDto(e);
            eventShortDto.setConfirmedRequests(getConfirmedRequests(e.getId()));
            eventShortDto.setViews(getViews(e.getPublishedOn(), e.getId()));
            return eventShortDto;
        }).toList();

        log.debug("MAIN: {} events were found.", eventShortDtos.size());
        return eventShortDtos;
    }

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
            throw new ForbiddenException("For the requested operation the conditions are not met.",
                    String.format("Field: eventDate. Error: Must contain a date that has not yet occurred. Value: %s.",
                            newEventDto.getEventDate()));

        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                "User with id = " + userId + " does not exist."));

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("There is no such category.",
                "Category with id = " + newEventDto.getCategory() + " does not exist."));

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setState(EventsStates.PENDING);
        event = eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(getViews(eventFullDto.getPublishedOn(), eventFullDto.getId()));

        log.debug("MAIN: {} was created.", event);
        return eventFullDto;
    }

    public EventFullDto getEvent(Long userId, Long eventId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(getViews(eventFullDto.getPublishedOn(), eventFullDto.getId()));

        log.debug("MAIN: {} was found.", event);
        return eventFullDto;
    }

    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventUpdate) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        if (!event.getInitiator().getId().equals(userId))
            throw new BadRequestException("User is not the initiator of the event.",
                    "Only the event initiator can perform this action.");

        if (event.getState() != EventsStates.PENDING && event.getState() != EventsStates.CANCELED)
            throw new ForbiddenException("Event state does not allow this action.",
                    "The action can only be performed on events in PENDING or CANCELED state.");

        if (!eventUpdate.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
            throw new ForbiddenException("Event date is too soon.",
                    "The event date must be at least 2 hours in the future.");

        Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(() ->
                new NotFoundException("There is no such category.",
                        "Category with id = " + eventUpdate.getCategory() + " does not exist."));

        if (eventUpdate.getAnnotation() != null) event.setAnnotation(eventUpdate.getAnnotation());
        if (eventUpdate.getCategory() != null) event.setCategory(category);
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getEventDate() != null) event.setEventDate(eventUpdate.getEventDate());
        if (eventUpdate.getLocation() != null) {
            event.setLat(eventUpdate.getLocation().getLat());
            event.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) event.setPaid(eventUpdate.getPaid());
        if (eventUpdate.getParticipantLimit() != null) event.setParticipantLimit(eventUpdate.getParticipantLimit());
        if (eventUpdate.getRequestModeration() != null) event.setRequestModeration(eventUpdate.getRequestModeration());
        if (eventUpdate.getStateAction() != null) {
            if (eventUpdate.getStateAction().equals(EventsStatesAction.SEND_TO_REVIEW))
                event.setState(EventsStates.PENDING);
            if (eventUpdate.getStateAction().equals(EventsStatesAction.CANCEL_REVIEW))
                event.setState(EventsStates.CANCELED);
        }
        if (eventUpdate.getTitle() != null) event.setTitle(eventUpdate.getTitle());

        event = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(getViews(eventFullDto.getPublishedOn(), eventFullDto.getId()));

        log.debug("MAIN: {} was updated.", event);
        return eventFullDto;
    }

    public List<ParticipationRequestDto> getRequestsToUserEvent(Long userId, Long eventId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        if (!eventRepository.existsById(eventId))
            throw new NotFoundException("There is no such event.",
                    "Event with id = " + eventId + " does not exist.");

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new BadRequestException("User is not the initiator of the event.",
                    "Only the event initiator can perform this action.");

        List<Participant> participants = participationRepository.findAllByEventIdAndStatus(userId,
                EventRequestStatus.PENDING);

        List<ParticipationRequestDto> participationRequestDtos = participants.stream()
                .map(participationMapper::toParticipationRequestDto)
                .toList();

        log.debug("MAIN: {} requests were found.", participationRequestDtos.size());
        return participationRequestDtos;
    }

    public EventRequestStatusUpdateResult handleRequestsToUserEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest body) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        if (!eventRepository.existsById(eventId))
            throw new NotFoundException("There is no such event.",
                    "Event with id = " + eventId + " does not exist.");

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new BadRequestException("User is not the initiator of the event.",
                    "Only the event initiator can perform this action.");

        if (body.getStatus() != EventRequestStatus.CONFIRMED && body.getStatus() != EventRequestStatus.REJECTED)
            throw new ForbiddenException("Invalid request status",
                    "The status can only be set to CONFIRMED or REJECTED.");

        List<Participant> participants = participationRepository.findAllById(body.getRequestIds());
        participants.forEach(e -> e.setStatus(body.getStatus()));
        participationRepository.saveAll(participants);

        List<ParticipationRequestDto> confirmed = participationRepository.findAllByEventIdAndStatus(eventId,
                EventRequestStatus.CONFIRMED).stream().map(participationMapper::toParticipationRequestDto).toList();
        List<ParticipationRequestDto> rejected = participationRepository.findAllByEventIdAndStatus(eventId,
                EventRequestStatus.REJECTED).stream().map(participationMapper::toParticipationRequestDto).toList();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmed, rejected);

        log.debug("MAIN: {} participants were updated.", participants.size());
        return result;
    }

    private Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    private Long getViews(LocalDateTime publishedOn, Long eventId) {
        return statsClient.getStats(publishedOn, LocalDateTime.now(),
                List.of(String.format("/events/%d", eventId)), false).getFirst().getHits();
    }
}
