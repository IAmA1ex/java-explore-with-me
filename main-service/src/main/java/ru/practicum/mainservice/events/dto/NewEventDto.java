package ru.practicum.mainservice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Annotation cannot be blank.")
    @Length(min = 20, max = 2000, message = "Length of the annotation should be from 20 to 7000.")
    private String annotation;

    @NotNull(message = "Category cannot be null.")
    private Long category;

    @NotBlank(message = "Description cannot be blank.")
    @Length(min = 20, max = 7000, message = "Length of the description should be from 20 to 7000.")
    private String description;

    @NotNull(message = "Event date cannot be null.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Location date cannot be null.")
    private LocationDto location;

    @JsonProperty(defaultValue = "false")
    private boolean paid = false;

    @PositiveOrZero(message = "Participant limit must be a positive number.")
    @JsonProperty(defaultValue = "0")
    private Long participantLimit = 0L;

    @JsonProperty(defaultValue = "true")
    private boolean requestModeration = true;

    @NotBlank(message = "Title cannot be blank.")
    @Length(min = 3, max = 120, message = "Length of the title should be from 3 to 120.")
    private String title;

}
