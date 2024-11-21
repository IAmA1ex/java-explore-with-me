package ru.practicum.mainservice.replies.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullReplyDto {

    private Long id;

    private LocalDateTime createdOn;

    private String author;

    private String text;

    private List<ReplyLikeDto> likes;
}
