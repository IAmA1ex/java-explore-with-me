package ru.practicum.mainervice.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoShort {

    private Long id;

    @NotBlank(message = "Name cannot be blank.")
    private String name;

}
