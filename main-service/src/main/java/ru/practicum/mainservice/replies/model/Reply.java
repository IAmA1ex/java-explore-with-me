package ru.practicum.mainservice.replies.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.comments.model.Comment;
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
    private Comment comment;

    private LocalDateTime createdOn;

    private String text;
}
