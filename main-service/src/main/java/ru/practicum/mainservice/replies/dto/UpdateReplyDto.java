package ru.practicum.mainservice.replies.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReplyDto {

    @NotBlank(message = "Reply message cannot be blank.")
    private String text;
}
