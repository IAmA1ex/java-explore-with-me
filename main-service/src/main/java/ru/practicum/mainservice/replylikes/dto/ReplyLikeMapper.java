package ru.practicum.mainservice.replylikes.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.replylikes.model.ReplyLike;

@Component
public class ReplyLikeMapper {

    public ReplyLikeDto toReplyLikeDto(final ReplyLike replyLike) {
        return ReplyLikeDto.builder()
                .user(replyLike.getUser().getName())
                .createdOn(replyLike.getCreatedOn())
                .build();
    }
}
