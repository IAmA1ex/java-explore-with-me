package ru.practicum.mainservice.categories.dto;

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
public class CategoryDto {

    private Long id;

    @Length(min = 1, max = 50, message = "Length of the name should be from 1 to 50.")
    @NotBlank(message = "Name cannot be blank.")
    private String name;
}
