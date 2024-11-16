package ru.practicum.mainservice.comments.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.comments.model.Comment;

import java.util.List;

@Component
public class CommentMapper {

    public Comment toComment(final NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public FullCommentDto toFullCommentDto(final Comment comment) {
        return FullCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .author(comment.getAuthor().getName())
                .likes(List.of())
                .replies(List.of())
                .build();
    }
}
