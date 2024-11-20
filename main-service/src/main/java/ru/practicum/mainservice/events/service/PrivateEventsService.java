package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.commentlikes.dao.CommentLikeRepository;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.dto.CommentMapper;
import ru.practicum.mainservice.comments.dto.NewCommentDto;
import ru.practicum.mainservice.comments.dto.UpdateCommentDto;
import ru.practicum.mainservice.comments.model.Comment;
import ru.practicum.mainservice.commentlikes.model.CommentLike;
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
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.NewReplyDto;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replies.dto.UpdateReplyDto;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.model.ReplyLike;
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
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final ReplyRepository replyRepository;
    private final EventMapper eventMapper;
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
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
            eventShortDto.setConfirmedRequests(sgf.getConfirmedRequests(e.getId()));
            eventShortDto.setComments(sgf.getCountOfComments(e.getId()));
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
        eventFullDto.setConfirmedRequests(sgf.getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setComments(sgf.getCountOfComments(eventFullDto.getId()));
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
        eventFullDto.setConfirmedRequests(sgf.getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setComments(sgf.getCountOfComments(eventFullDto.getId()));
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
        eventFullDto.setConfirmedRequests(sgf.getConfirmedRequests(eventFullDto.getId()));
        eventFullDto.setComments(sgf.getCountOfComments(eventFullDto.getId()));
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

    public FullCommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {

        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                        "User with id = " + userId + " does not exist."));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("There is no such event.",
                        "Event with id = " + eventId + " does not exist."));

        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setCreatedOn(LocalDateTime.now());
        comment.setEvent(event);
        comment.setAuthor(author);

        Comment createdComment = commentRepository.save(comment);
        log.debug("MAIN: {} was created.", createdComment);
        return commentMapper.toFullCommentDto(createdComment);
    }

    public FullCommentDto updateComment(Long userId, Long eventId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentCorrectnessRequestCheck(userId, eventId, commentId);
        comment.setText(updateCommentDto.getText());
        Comment updatedComment = commentRepository.save(comment);
        log.debug("MAIN: {} was updated.", updatedComment);
        return commentMapper.toFullCommentDto(updatedComment);
    }

    public void deleteComment(Long userId, Long eventId, Long commentId) {
        commentCorrectnessRequestCheck(userId, eventId, commentId);
        commentRepository.deleteById(commentId);
        log.debug("MAIN: {} was deleted.", commentId);
    }

    public FullReplyDto createReply(Long userId, Long eventId, Long commentId, NewReplyDto newReplyDto) {

        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                        "User with id = " + userId + " does not exist."));

        Comment comment = sgf.commentToEventCheck(eventId, commentId);

        Reply reply = replyMapper.toReply(newReplyDto);
        reply.setCreatedOn(LocalDateTime.now());
        reply.setComment(comment);
        reply.setAuthor(author);

        Reply createdReply = replyRepository.save(reply);
        log.debug("MAIN: {} was created.", createdReply);
        return replyMapper.toFullReplyDto(createdReply);
    }

    public FullReplyDto updateReply(Long userId, Long eventId, Long commentId,
                                    Long replyId, UpdateReplyDto updateReplyDto) {
        Reply reply = replyCorrectnessRequestCheck(userId, eventId, commentId, replyId);
        reply.setText(updateReplyDto.getText());
        Reply updatedReply = replyRepository.save(reply);
        log.debug("MAIN: {} was updated.", updatedReply);
        return replyMapper.toFullReplyDto(updatedReply);
    }

    public void deleteReply(Long userId, Long eventId, Long commentId, Long replyId) {
        replyCorrectnessRequestCheck(userId, eventId, commentId, replyId);
        replyRepository.deleteById(replyId);
        log.debug("MAIN: {} was deleted.", replyId);
    }

    public FullCommentDto setLikeComment(Long userId, Long eventId, Long commentId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                        "User with id = " + userId + " does not exist."));

        Comment comment = sgf.commentToEventCheck(eventId, commentId);

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .createdOn(LocalDateTime.now())
                .build();

        CommentLike savedCommentLike = commentLikeRepository.save(commentLike);
        log.debug("MAIN: {} was created.", savedCommentLike);
        FullCommentDto fullCommentDto = commentMapper.toFullCommentDto(comment);
        sgf.fillFullCommentDto(fullCommentDto);
        return fullCommentDto;
    }

    @Transactional
    public FullCommentDto removeLikeComment(Long userId, Long eventId, Long commentId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        Comment comment = sgf.commentToEventCheck(eventId, commentId);

        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new NotFoundException("There is no such like.",
                    "User with id = " + userId + " does not set like to comment with id = " + commentId + ".");
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.debug("MAIN: {} was removed.", commentId);
        FullCommentDto fullCommentDto = commentMapper.toFullCommentDto(comment);
        sgf.fillFullCommentDto(fullCommentDto);
        return fullCommentDto;
    }

    public FullReplyDto setLikeReply(Long userId, Long eventId, Long commentId, Long replyId) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("There is no such user.",
                        "User with id = " + userId + " does not exist."));

        sgf.commentToEventCheck(eventId, commentId);

        Reply reply = sgf.replyToCommentCheck(commentId, replyId);

        ReplyLike replyLike = ReplyLike.builder()
                .user(user)
                .reply(reply)
                .createdOn(LocalDateTime.now())
                .build();

        ReplyLike savedReplyLike = replyLikeRepository.save(replyLike);
        log.debug("MAIN: {} was created.", savedReplyLike);
        FullReplyDto fullReplyDto = replyMapper.toFullReplyDto(reply);
        sgf.fillFullReplyDto(fullReplyDto);
        return fullReplyDto;
    }

    @Transactional
    public FullReplyDto removeLikeReply(Long userId, Long eventId, Long commentId, Long replyId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        sgf.commentToEventCheck(eventId, commentId);

        Reply reply = sgf.replyToCommentCheck(commentId, replyId);

        if (!replyLikeRepository.existsByReplyIdAndUserId(replyId, userId)) {
            throw new NotFoundException("There is no such like.",
                    "User with id = " + userId + " does not set like to reply with id = " + replyId  + ".");
        }

        replyLikeRepository.deleteByReplyIdAndUserId(replyId, userId);
        log.debug("MAIN: {} was removed.", replyId);
        FullReplyDto fullReplyDto = replyMapper.toFullReplyDto(reply);
        sgf.fillFullReplyDto(fullReplyDto);
        return fullReplyDto;
    }

    private Comment commentCorrectnessRequestCheck(Long userId, Long eventId, Long commentId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        Comment comment = sgf.commentToEventCheck(eventId, commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("The user cannot edit this comment.",
                    "The user with id = " + userId + " cannot edit the comment with id = " + commentId + ".");
        }

        return comment;
    }

    private Reply replyCorrectnessRequestCheck(Long userId, Long eventId, Long commentId, Long replyId) {

        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");

        sgf.commentToEventCheck(eventId, commentId);

        Reply reply = sgf.replyToCommentCheck(commentId, replyId);

        if (!reply.getAuthor().getId().equals(userId)) {
            throw new BadRequestException("The user cannot edit this reply.",
                    "The user with id = " + userId + " cannot edit the reply with id = " + replyId + ".");
        }

        return reply;
    }

}
