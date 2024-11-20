package ru.practicum.mainservice.commentlikes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.commentlikes.model.CommentLike;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findAllByCommentId(Long id);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
