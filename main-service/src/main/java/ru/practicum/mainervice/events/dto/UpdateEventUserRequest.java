package ru.practicum.mainervice.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.mainervice.events.model.EventsStatesAction;
import ru.practicum.mainervice.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {

    @Length(min = 20, max = 2000, message = "Length of the annotation should be from 20 to 7000.")
    private String annotation;

    private Long category;

    @Length(min = 20, max = 7000, message = "Length of the description should be from 20 to 7000.")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventsStatesAction stateAction;

    @Length(min = 3, max = 120, message = "Length of the title should be from 3 to 120.")
    private String title;

}
