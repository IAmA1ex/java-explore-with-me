package ru.practicum.mainservice.replies.model;

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
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    @ManyToOne
    private Event event;

    private LocalDateTime createdOn;

    private String text;
}
