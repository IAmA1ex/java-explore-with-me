package ru.practicum.mainservice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    @Length(min = 20, max = 2000, message = "Length of the annotation should be from 20 to 7000.")
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000, message = "Length of the description should be from 20 to 7000.")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Participant limit must be a positive number.")
    private Long participantLimit;

    private Boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventsStatesAction stateAction;

    @Length(min = 3, max = 120, message = "Length of the title should be from 3 to 120.")
    private String title;

}
