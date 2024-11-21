package ru.practicum.mainservice;

import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.dto.NewCommentDto;
import ru.practicum.mainservice.comments.dto.ShortCommentDto;
import ru.practicum.mainservice.comments.dto.UpdateCommentDto;
import ru.practicum.mainservice.comments.model.Comment;
import ru.practicum.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.mainservice.compilations.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilations.model.Compilation;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.location.dto.LocationDto;
import ru.practicum.mainservice.participants.model.Participant;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.NewReplyDto;
import ru.practicum.mainservice.replies.dto.UpdateReplyDto;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RandomStuff {

    public static NewUserRequest getNewUserRequest(Long id) {
        return NewUserRequest.builder()
                .name("name" + id)
                .email("email" + id + "@email.com")
                .build();
    }

    public static User getUser(Long id) {
        return User.builder()
                .id(id)
                .name("name" + id)
                .email("email" + id + "@email.com")
                .build();
    }

    public static CategoryDto getCategoryDto(Long categoryId) {
        return CategoryDto.builder()
                .id(categoryId)
                .name("name" + categoryId)
                .build();
    }

    public static Category getCategory(Long categoryId) {
        return Category.builder()
                .id(categoryId)
                .name("name" + categoryId)
                .build();
    }

    public static NewEventDto getNewEventDto(Long unique, Long categoryId, Long userId) {
        return NewEventDto.builder()
                .annotation("annotation annotation annotation annotation annotation " + unique)
                .category(categoryId)
                .description("description description description description description " + unique)
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(LocationDto.builder()
                        .lat(categoryId + 0.12345)
                        .lon(userId + 0.09876)
                        .build())
                .paid(true)
                .participantLimit(5L)
                .requestModeration(false)
                .title("title title title title title " + unique)
                .build();
    }

    public static UpdateEventAdminRequest getUpdateEventAdminRequest(Long unique, Long categoryId, Long userId) {
        return UpdateEventAdminRequest.builder()
                .annotation("annotation update annotation update annotation update " + unique)
                .category(categoryId)
                .description("description update description update description update " + unique)
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(LocationDto.builder()
                        .lat(categoryId + 0.12345)
                        .lon(userId + 0.09876)
                        .build())
                .paid(true)
                .participantLimit(5L)
                .requestModeration(false)
                .title("title update title update title update " + unique)
                .stateAction(EventsStatesAction.PUBLISH_EVENT)
                .build();
    }

    public static UpdateEventUserRequest getUpdateEventUserRequest(Long unique, Long categoryId, Long userId) {
        return UpdateEventUserRequest.builder()
                .annotation("annotation update annotation update annotation update " + unique)
                .category(categoryId)
                .description("description update description update description update " + unique)
                .eventDate(LocalDateTime.now().plusDays(1))
                .location(LocationDto.builder()
                        .lat(categoryId + 0.12345)
                        .lon(userId + 0.09876)
                        .build())
                .paid(true)
                .participantLimit(5L)
                .requestModeration(false)
                .title("title update title update title update " + unique)
                .stateAction(EventsStatesAction.PUBLISH_EVENT)
                .build();
    }

    public static Event getEvent(Long id, Long initiatorId, Long categoryId) {
        return Event.builder()
                .id(id)
                .initiator(getUser(initiatorId))
                .annotation("annotation annotation annotation annotation annotation annotation " + id)
                .category(getCategory(categoryId))
                .description("description description description description description description " + id)
                .eventDate(LocalDateTime.now().plusDays(5))
                .createdOn(LocalDateTime.now().minusDays(5))
                .publishedOn(LocalDateTime.now().minusDays(3))
                .lat(Double.valueOf("12." + System.currentTimeMillis()))
                .lon(Double.valueOf("27." + System.currentTimeMillis()))
                .paid(true)
                .participantLimit(5L)
                .requestModeration(false)
                .state(EventsStates.PUBLISHED)
                .title("title title title title title " + id)
                .build();
    }

    public static Participant getParticipant(Long id, Long eventId, Long requesterId) {
        return Participant.builder()
                .id(id)
                .event(getEvent(eventId, 1L, 1L))
                .requester(getUser(requesterId))
                .created(LocalDateTime.now())
                .status(EventRequestStatus.PENDING)
                .build();
    }

    public static Participant getParticipant(Long id, Long eventId, Long requesterId, EventRequestStatus status) {
        return Participant.builder()
                .id(id)
                .event(getEvent(eventId, 1L, 1L))
                .requester(getUser(requesterId))
                .created(LocalDateTime.now())
                .status(status)
                .build();
    }

    public static EventRequestStatusUpdateRequest getEventRequestStatusUpdateRequest(
            List<Long> requesterIds, EventRequestStatus status) {
        return EventRequestStatusUpdateRequest.builder()
                .requestIds(requesterIds)
                .status(status)
                .build();
    }

    public static NewCompilationDto getNewCompilationDto() {
        return NewCompilationDto.builder()
                .events(List.of(1L))
                .pinned(false)
                .title("title title title title")
                .build();
    }

    public static UpdateCompilationRequest getUpdateCompilationRequest() {
        return UpdateCompilationRequest.builder()
                .events(List.of(1L))
                .pinned(false)
                .title("update title update title update title")
                .build();
    }

    public static Compilation getCompilation(Long id) {
        return Compilation.builder()
                .id(id)
                .pinned(true)
                .title("title title title title")
                .build();
    }

    public static FullCommentDto getFullCommentDto(Long id) {
        return FullCommentDto.builder()
                .id(id)
                .createdOn(LocalDateTime.now())
                .author("author")
                .text("text text text text text")
                .likes(List.of())
                .replies(List.of())
                .build();
    }

    public static NewCommentDto getNewCommentDto() {
        return NewCommentDto.builder()
                .text("text text text text text")
                .build();
    }

    public static UpdateCommentDto getUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .text("update text text text text text")
                .build();
    }

    public static FullReplyDto getFullReplyDto(Long id) {
        return FullReplyDto.builder()
                .id(id)
                .createdOn(LocalDateTime.now())
                .author("author")
                .text("text text text text text")
                .likes(List.of())
                .build();
    }

    public static NewReplyDto getNewReplyDto() {
        return NewReplyDto.builder()
                .text("text text text text text")
                .build();
    }

    public static UpdateReplyDto getUpdateReplyDto() {
        return UpdateReplyDto.builder()
                .text("text text text text text")
                .build();
    }

    public static ShortCommentDto getShortCommentDto(Long id) {
        return ShortCommentDto.builder()
                .id(id)
                .createdOn(LocalDateTime.now())
                .author("author")
                .text("text text text text text")
                .likes(0L)
                .replies(0L)
                .build();
    }

    public static Comment getComment(Long id, Long eventId, Long initiatorId, Long categoryId, Long authorId) {
        return Comment.builder()
                .id(id)
                .event(getEvent(eventId, initiatorId, categoryId))
                .author(getUser(authorId))
                .createdOn(LocalDateTime.now())
                .text("text text text text text")
                .build();
    }

    public static Reply getReply(Long id, Long commentId, Long eventId, Long initiatorId, Long categoryId,
                                 Long commentAuthorId, Long authorId) {
        return Reply.builder()
                .id(id)
                .comment(getComment(commentId, eventId, initiatorId, categoryId, commentAuthorId))
                .author(getUser(authorId))
                .createdOn(LocalDateTime.now())
                .text("text text text text text")
                .build();
    }
}
