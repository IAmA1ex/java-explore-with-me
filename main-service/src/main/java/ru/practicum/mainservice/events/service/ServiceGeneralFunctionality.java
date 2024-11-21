package ru.practicum.mainservice.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.commentlikes.dao.CommentLikeRepository;
import ru.practicum.mainservice.commentlikes.dto.CommentLikeDto;
import ru.practicum.mainservice.commentlikes.dto.CommentLikesMapper;
import ru.practicum.mainservice.commentlikes.model.CommentLike;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.model.Comment;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.UpdateEventRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replies.dto.ShortReplyDto;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeDto;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeMapper;
import ru.practicum.mainservice.replylikes.model.ReplyLike;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceGeneralFunctionality {

    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final CommentLikesMapper commentLikesMapper;
    private final ReplyMapper replyMapper;
    private final ReplyLikeMapper replyLikeMapper;

    public void updateEvent(Event event, UpdateEventRequest eventUpdate) {
        if (eventUpdate.getAnnotation() != null) event.setAnnotation(eventUpdate.getAnnotation());
        if (eventUpdate.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdate.getCategory()).orElseThrow(() ->
                    new NotFoundException("There is no such category.",
                            "Category with id = " + eventUpdate.getCategory() + " does not exist."));
            event.setCategory(category);
        }
        if (eventUpdate.getDescription() != null) event.setDescription(eventUpdate.getDescription());
        if (eventUpdate.getLocation() != null) {
            event.setLat(eventUpdate.getLocation().getLat());
            event.setLon(eventUpdate.getLocation().getLon());
        }
        if (eventUpdate.getPaid() != null) event.setPaid(eventUpdate.getPaid());
        if (eventUpdate.getParticipantLimit() != null) event.setParticipantLimit(eventUpdate.getParticipantLimit());
        if (eventUpdate.getRequestModeration() != null) event.setRequestModeration(eventUpdate.getRequestModeration());
        if (eventUpdate.getTitle() != null) event.setTitle(eventUpdate.getTitle());
    }

    public Long getConfirmedRequests(Long eventId) {
        return eventRepository.countOfParticipants(eventId);
    }

    public Long getCountOfComments(Long eventId) {
        return eventRepository.getCountOfComments(eventId);
    }

    public void fillFullCommentDto(FullCommentDto fullCommentDto) {
        List<CommentLike> commentLikes = commentLikeRepository.findAllByCommentId(fullCommentDto.getId());
        List<CommentLikeDto> commentLikeDtos = commentLikes.stream()
                .map(commentLikesMapper::toCommentLikeDto)
                .toList();
        fullCommentDto.setLikes(commentLikeDtos);
        List<ShortReplyDto> shortReplyDtos = replyRepository.findAllByCommentId(fullCommentDto.getId());
        fullCommentDto.setReplies(shortReplyDtos);
    }

    public void fillFullReplyDto(FullReplyDto fullReplyDto) {
        List<ReplyLike> replyLikes = replyLikeRepository.findAllByReplyId(fullReplyDto.getId());
        List<ReplyLikeDto> replyLikeDtos = replyLikes.stream().map(replyLikeMapper::toReplyLikeDto).toList();
        fullReplyDto.setLikes(replyLikeDtos);
    }

    public Comment commentToEventCheck(Long eventId, Long commentId) {

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("There is no such event.",
                    "Event with id = " + eventId + " does not exist.");
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("There is no such comment.",
                        "Comment with id = " + commentId + " does not exist."));

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new BadRequestException("The event does not contain such a comment.",
                    "The event with id = " + eventId + " does not contain a comment with id = " + commentId + ".");
        }

        return comment;
    }

    public Reply replyToCommentCheck(Long commentId, Long replyId) {

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("There is no such comment.",
                    "Comment with id = " + commentId + " does not exist.");
        }

        Reply reply = replyRepository.findById(replyId).orElseThrow(() ->
                new NotFoundException("There is no such reply.",
                        "Reply with id = " + replyId + " does not exist."));

        if (!reply.getComment().getId().equals(commentId)) {
            throw new BadRequestException("The comment does not contain such a reply.",
                    "The comment with id = " + commentId + " does not contain a reply with id = " + replyId + ".");
        }

        return reply;
    }
}
