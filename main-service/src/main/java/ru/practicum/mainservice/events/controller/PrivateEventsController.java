package ru.practicum.mainservice.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.dto.NewCommentDto;
import ru.practicum.mainservice.comments.dto.UpdateCommentDto;
import ru.practicum.mainservice.events.service.PrivateEventsService;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.replies.dto.NewReplyDto;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.UpdateReplyDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {

    private final PrivateEventsService privateEventsService;

    @GetMapping
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") final Long from,
                                                      @RequestParam(defaultValue = "10") final Long size) {
        return privateEventsService.getEventsCreatedByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @RequestBody @Valid final NewEventDto newEventDto) {
        return privateEventsService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable("userId") Long userId,
                                 @PathVariable("eventId") Long eventId) {
        return privateEventsService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid final UpdateEventUserRequest updateEventUserRequest) {
        return privateEventsService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsToUserEvent(@PathVariable("userId") Long userId,
                                                                @PathVariable("eventId") Long eventId) {
        return privateEventsService.getRequestsToUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult handleRequestsToUserEvent(@PathVariable("userId") Long userId,
                                                                    @PathVariable("eventId") Long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest body) {
        return privateEventsService.handleRequestsToUserEvent(userId, eventId, body);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public FullCommentDto createComment(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @RequestBody @Valid final NewCommentDto newCommentDto) {
        return privateEventsService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public FullCommentDto updateComment(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestBody @Valid final UpdateCommentDto updateCommentDto) {
        return privateEventsService.updateComment(userId, eventId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId,
                              @PathVariable("commentId") Long commentId) {
        privateEventsService.deleteComment(userId, eventId, commentId);
    }

    @PostMapping("/{eventId}/comments/{commentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public FullReplyDto createReply(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody @Valid final NewReplyDto newReplyDto) {
        return privateEventsService.createReply(userId, eventId, commentId, newReplyDto);
    }

    @PatchMapping("/{eventId}/comments/{commentId}/replies/{replyId}")
    public FullReplyDto updateReply(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @PathVariable("commentId") Long commentId,
                                    @PathVariable("replyId") Long replyId,
                                    @RequestBody @Valid final UpdateReplyDto updateReplyDto) {
        return privateEventsService.updateReply(userId, eventId, commentId, replyId, updateReplyDto);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}/replies/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReply(@PathVariable("userId") Long userId,
                              @PathVariable("eventId") Long eventId,
                              @PathVariable("commentId") Long commentId,
                              @PathVariable("replyId") Long replyId) {
        privateEventsService.deleteReply(userId, eventId, commentId, replyId);
    }

    @PostMapping("/{eventId}/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public FullCommentDto setLikeComment(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId,
                                         @PathVariable("commentId") Long commentId) {
        return privateEventsService.setLikeComment(userId, eventId, commentId);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public FullCommentDto removeLikeComment(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId,
                                            @PathVariable("commentId") Long commentId) {
        return privateEventsService.removeLikeComment(userId, eventId, commentId);
    }

    @PostMapping("/{eventId}/comments/{commentId}/replies/{replyId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public FullReplyDto setLikeReply(@PathVariable("userId") Long userId,
                                     @PathVariable("eventId") Long eventId,
                                     @PathVariable("commentId") Long commentId,
                                     @PathVariable("replyId") Long replyId) {
        return privateEventsService.setLikeReply(userId, eventId, commentId, replyId);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}/replies/{replyId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public FullReplyDto removeLikeReply(@PathVariable("userId") Long userId,
                                        @PathVariable("eventId") Long eventId,
                                        @PathVariable("commentId") Long commentId,
                                        @PathVariable("replyId") Long replyId) {
        return privateEventsService.removeLikeReply(userId, eventId, commentId, replyId);
    }
}
