package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.participants.dao.ParticipationRepository;
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.participants.model.Participant;
import ru.practicum.mainservice.user.dao.UserRepository;
import ru.practicum.mainservice.user.model.User;

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
    private final ParticipationMapper participationMapper;
    private final ServiceGeneralFunctionality sgf;
    private final StatsGeneralFunctionality agf;

    public List<EventShortDto> getEventsCreatedByUser(Long userId, Long from, Long size) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        List<Event> events = eventRepository.findAllByInitiatorIdSorted(userId, from, size);

        List<EventShortDto> eventShortDtos = events.stream().map(e -> {
            EventShortDto eventShortDto = eventMapper.toEventShortDto(e);
            eventShortDto.setConfirmedRequests(getConfirmedRequests(e.getId()));
            eventShortDto.setViews(agf.getViews(e.getCreatedOn(),
                    String.format("/events/%d", e.getId()), false));
            return eventShortDto;
        }).toList();

        log.debug("MAIN: {} events were found.", eventShortDtos.size());
        return eventShortDtos;
    }

    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
            throw new BadRequestException("For the requested operation the conditions are not met.",
                    String.format("Event date must contain a date that has not yet occurred. Value: %s.",
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
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(agf.getViews(eventFullDto.getCreatedOn(),
                String.format("/events/%d", eventFullDto.getId()), false));

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

        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Access is denied.", "This user does not have access to this event.");
        }

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(agf.getViews(eventFullDto.getCreatedOn(),
                String.format("/events/%d", eventFullDto.getId()), false));

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
            throw new ConflictException("Event state does not allow this action.",
                    "The action can only be performed on events in PENDING or CANCELED state.");

        if (eventUpdate.getEventDate() != null) {
            if (!eventUpdate.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
                throw new BadRequestException("Event date is too soon.",
                        "The event date must be at least 2 hours in the future.");
            event.setEventDate(eventUpdate.getEventDate());
        }

        sgf.updateEvent(event, eventUpdate);
        if (eventUpdate.getStateAction() != null) {
            if (eventUpdate.getStateAction().equals(EventsStatesAction.SEND_TO_REVIEW))
                event.setState(EventsStates.PENDING);
            if (eventUpdate.getStateAction().equals(EventsStatesAction.CANCEL_REVIEW))
                event.setState(EventsStates.CANCELED);
        }

        event = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setViews(agf.getViews(eventFullDto.getCreatedOn(),
                String.format("/events/%d", eventFullDto.getId()), false));

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

        List<Participant> participants = participationRepository.findAllByEventIdAndStatus(eventId,
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

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new BadRequestException("User is not the initiator of the event.",
                    "Only the event initiator can perform this action.");

        if (body.getStatus() != EventRequestStatus.CONFIRMED && body.getStatus() != EventRequestStatus.REJECTED)
            throw new BadRequestException("Invalid request status.",
                    "The status can only be set to CONFIRMED or REJECTED.");

        long vacancyCount = event.getParticipantLimit() - eventRepository.countOfParticipants(eventId);
        List<Participant> participants = participationRepository.findAllById(body.getRequestIds());
        for (Participant participant : participants) {
            if (body.getStatus().equals(EventRequestStatus.REJECTED)) {
                if (!participant.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                    participant.setStatus(EventRequestStatus.REJECTED);
                } else {
                    throw new ConflictException("Invalid status change", "Cannot change participant status to REJECTED.");
                }
            }
            if (body.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                if (vacancyCount > 0) {
                    participant.setStatus(EventRequestStatus.CONFIRMED);
                    vacancyCount--;
                } else {
                    throw new ConflictException(
                            "Participation limit reached.",
                            "Event with id = " + eventId + " has reached the maximum number of participants.");
                }
            }
        }
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
}
