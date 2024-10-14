package ru.practicum.mainervice.participants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.events.dao.EventRepository;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.events.model.EventRequestStatus;
import ru.practicum.mainervice.exception.errors.ConflictException;
import ru.practicum.mainervice.exception.errors.NotFoundException;
import ru.practicum.mainervice.participants.dao.ParticipationRepository;
import ru.practicum.mainervice.participants.dto.ParticipationMapper;
import ru.practicum.mainervice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainervice.participants.model.Participant;
import ru.practicum.mainervice.user.dao.UserRepository;
import ru.practicum.mainervice.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateParticipantsService {

    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationMapper participationMapper;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        List<Participant> participants = participationRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> participationRequestDtos = participants.stream()
                .map(participationMapper::toParticipationRequestDto)
                .toList();

        log.debug("MAIN: {} requests were found.", participationRequestDtos.size());
        return participationRequestDtos;
    }

    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                        "User with id = " + userId + " does not exist."));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        if (participationRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new ConflictException("Duplicate participation request.",
                    "User with id = " + userId + " has already requested to participate in event with id = " + eventId);

        if (eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new ConflictException(
                    "Event initiator cannot participate in own event.",
                    "User with id = " + userId + " is the initiator of event with id = " + eventId +
                            " and cannot request participation."
            );

        if (!eventRepository.existsByIdAndStateId(eventId, 2L))
            throw new ConflictException(
                    "Cannot participate in an unpublished event.",
                    "Event with id = " + eventId + " is not in the 'PUBLISHED' state."
            );

        if (!eventRepository.isRequestLimitNotReached(eventId))
            throw new ConflictException(
                    "Participation limit reached.",
                    "Event with id = " + eventId + " has reached the maximum number of participants."
            );

        Participant participant = Participant.builder()
                .event(event)
                .requester(user)
                .status(event.isRequestModeration() ? EventRequestStatus.PENDING : EventRequestStatus.CONFIRMED)
                .build();
        participant = participationRepository.save(participant);

        log.debug("MAIN: {} was created.", participant);
        return participationMapper.toParticipationRequestDto(participant);
    }

    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        Participant participant = participationRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("There is no such request.",
                    "Request with id = " + requestId + " does not exist."));

        if (!participationRepository.existsByIdAndRequesterId(requestId, userId))
            throw new ConflictException(
                    "Access denied.",
                    "User with id = " + userId + " is not the owner of participation request with id = " +
                            requestId + "."
            );

        if (participationRepository.existsByIdAndStatusId(requestId, 3L))
            throw new ConflictException("Request is already canceled.",
                    "Participation request with id = " + requestId + " has already been canceled."
            );

        participant.setStatus(EventRequestStatus.REJECTED);
        participant = participationRepository.save(participant);
        log.debug("MAIN: {} was cancelled.", participant);
        return participationMapper.toParticipationRequestDto(participant);
    }
}
