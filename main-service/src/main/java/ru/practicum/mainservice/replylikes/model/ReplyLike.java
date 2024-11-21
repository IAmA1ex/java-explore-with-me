package ru.practicum.mainservice.replylikes.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "replies_likes")
public class ReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Reply reply;

    @ManyToOne
    private User user;

    private LocalDateTime createdOn;
}
