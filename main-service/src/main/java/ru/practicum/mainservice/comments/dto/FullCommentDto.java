package ru.practicum.mainservice.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.commentlikes.dto.CommentLikeDto;
import ru.practicum.mainservice.replies.dto.ShortReplyDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullCommentDto {

    private Long id;

    private LocalDateTime createdOn;

    private String author;

    private String text;

    private List<CommentLikeDto> likes;

    private List<ShortReplyDto> replies;

}
