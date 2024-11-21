package ru.practicum.mainservice.replylikes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyLikeDto {

    private String user;

    private LocalDateTime createdOn;
}
