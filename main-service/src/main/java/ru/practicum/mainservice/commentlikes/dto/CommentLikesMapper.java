package ru.practicum.mainservice.commentlikes.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.commentlikes.model.CommentLike;

@Component
public class CommentLikesMapper {

    public CommentLikeDto toCommentLikeDto(CommentLike commentLike) {
        return CommentLikeDto.builder()
                .user(commentLike.getUser().getName())
                .createdOn(commentLike.getCreatedOn())
                .build();
    }
}
