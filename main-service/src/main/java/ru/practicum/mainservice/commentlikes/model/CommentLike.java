package ru.practicum.mainservice.commentlikes.model;

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
@Table(name = "comments_likes")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private User user;

    private LocalDateTime createdOn;
}
