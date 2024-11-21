package ru.practicum.mainservice.replies.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.replies.model.Reply;

import java.util.List;

@Component
public class ReplyMapper {

    public Reply toReply(final NewReplyDto newReplyDto) {
        return Reply.builder()
                .text(newReplyDto.getText())
                .build();
    }

    public FullReplyDto toFullReplyDto(final Reply reply) {
        return FullReplyDto.builder()
                .id(reply.getId())
                .text(reply.getText())
                .author(reply.getAuthor().getName())
                .createdOn(reply.getCreatedOn())
                .likes(List.of())
                .build();
    }
}
