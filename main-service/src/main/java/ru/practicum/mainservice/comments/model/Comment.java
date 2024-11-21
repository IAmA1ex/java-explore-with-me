package ru.practicum.mainservice.comments.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private User author;

    private LocalDateTime createdOn;

    private String text;
}
