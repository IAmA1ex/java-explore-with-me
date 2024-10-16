package ru.practicum.mainservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Name cannot be blank.")
    @Length(min = 2, max = 250, message = "Name must be between 2 and 250 characters.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email is not correct.")
    @Length(min = 6, max = 254, message = "Email must be between 6 and 254 characters.")
    private String email;
}
