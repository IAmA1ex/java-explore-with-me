package ru.practicum.mainservice.participants.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.events.dto.converters.EventRequestStatusConverter;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private User requester;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Convert(converter = EventRequestStatusConverter.class)
    private EventRequestStatus status;
}
