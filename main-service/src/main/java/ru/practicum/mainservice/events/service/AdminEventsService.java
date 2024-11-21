package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.StatsGeneralFunctionality;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.replies.dao.ReplyRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventsService {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final EventMapper eventMapper;
    private final ServiceGeneralFunctionality sgf;
    private final StatsGeneralFunctionality agf;

    public List<EventFullDto> getEvents(List<Long> users,
                                        List<EventsStates> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Long from,
                                        Long size) {
        List<Event> events = eventRepository.findAllByAdminFilters(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size);
        List<EventFullDto> eventFullDtos = events.stream().map(e -> {
            EventFullDto eventFullDto = eventMapper.toEventFullDto(e);
            eventFullDto.setConfirmedRequests(sgf.getConfirmedRequests(e.getId()));
            eventFullDto.setComments(sgf.getCountOfComments(e.getId()));
            eventFullDto.setViews(agf.getViews(e.getCreatedOn(), String.format("/events/%d", e.getId()), false));
            return eventFullDto;
        }).toList();
        log.debug("MAIN: {} were found.", events.size());
        return eventFullDtos;
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest eventUpdate) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        if (eventUpdate.getEventDate() != null &&
                !eventUpdate.getEventDate().isAfter(LocalDateTime.now().plusHours(2)))
            throw new BadRequestException("Event date is too soon.",
                    "The event date must be at least 2 hours in the future.");

        sgf.updateEvent(event, eventUpdate);
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
        eventFullDto.setConfirmedRequests(sgf.getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setComments(sgf.getCountOfComments(eventFullDto.getId()));
        eventFullDto.setViews(agf.getViews(eventFullDto.getCreatedOn(),
                String.format("/events/%d", eventFullDto.getId()), false));

        log.debug("MAIN: {} was updated.", event);
        return eventFullDto;
    }

    public void deleteComment(Long eventId, Long commentId) {
        sgf.commentToEventCheck(eventId, commentId);
        commentRepository.deleteById(commentId);
    }

    public void deleteReply(Long eventId, Long commentId, Long replyId) {
        sgf.commentToEventCheck(eventId, commentId);
        if (!replyRepository.isBelongsToComment(replyId, commentId)) {
            throw new BadRequestException("The comment does not contain such a reply.",
                    "The comment with id = " + commentId + " does not contain a reply with id = " + replyId + ".");
        }
        replyRepository.deleteById(replyId);
    }
}
