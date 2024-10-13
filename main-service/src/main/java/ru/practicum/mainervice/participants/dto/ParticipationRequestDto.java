package ru.practicum.mainervice.participants.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.jdi.request.EventRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainervice.events.model.EventRequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventRequestStatus status;
}
