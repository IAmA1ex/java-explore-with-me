package ru.practicum.mainservice.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.events.dto.converters.EventsStateConverter;
import ru.practicum.mainservice.user.model.User;

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

    @Column(columnDefinition = "VARCHAR(2000)")
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(columnDefinition = "VARCHAR(7000)")
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
    @Convert(converter = EventsStateConverter.class)
    private EventsStates state;

    private String title;

}
