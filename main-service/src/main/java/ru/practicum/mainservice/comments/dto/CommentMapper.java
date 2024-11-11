package ru.practicum.mainservice.comments.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.comments.model.Comment;

import java.util.List;

@Component
public class CommentMapper {

    public CommentDto toCommentDto(final Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .createdOn(comment.getCreatedOn())
                .commentator(comment.getCommentator().getName())
                .text(comment.getText())
                .replies(List.of())
                .build();
    }


}
