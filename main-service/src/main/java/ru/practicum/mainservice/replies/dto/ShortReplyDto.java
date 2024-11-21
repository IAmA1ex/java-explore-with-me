package ru.practicum.mainservice.replies.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortReplyDto {

    private Long id;

    private LocalDateTime createdOn;

    private String author;

    private String text;

    private Long likes;
}
