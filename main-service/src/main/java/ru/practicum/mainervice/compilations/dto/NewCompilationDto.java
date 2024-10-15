package ru.practicum.mainervice.compilations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    @NotNull(message = "Event list cannot be null.")
    @NotEmpty(message = "Event list cannot be empty.")
    @UniqueElements(message = "Event list must contain unique elements.")
    private List<Long> events;

    @JsonProperty(defaultValue = "false")
    private Boolean pinned;

    @Length(min = 1, max = 50, message = "Title must be between 1 and 50 characters.")
    private String title;
}
