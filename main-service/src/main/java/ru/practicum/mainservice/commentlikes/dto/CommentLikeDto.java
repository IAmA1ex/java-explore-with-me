package ru.practicum.mainservice.commentlikes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeDto {

    private String user;

    private LocalDateTime createdOn;
}
