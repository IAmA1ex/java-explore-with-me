package ru.practicum.mainervice.events.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainervice.user.model.User;

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
}
