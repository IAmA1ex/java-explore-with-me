package ru.practicum.mainervice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email is not correct.")
    private String email;

}
