package ru.practicum.mainervice.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainervice.categories.model.Category;
import ru.practicum.mainervice.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User initiator;

    private String annotation;

    @ManyToOne
    private Category category;

    private String description;

    private LocalDateTime eventDate;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private Double lat;

    private Double lon;

    private boolean paid;

    private Long participantLimit;

    private boolean requestModeration;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventsStates state;

    private String title;

}
