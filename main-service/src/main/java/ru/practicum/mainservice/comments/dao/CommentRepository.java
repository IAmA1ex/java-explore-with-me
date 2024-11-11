package ru.practicum.mainservice.comments.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
