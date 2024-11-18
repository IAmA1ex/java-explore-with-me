package ru.practicum.mainservice.commentlikes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.commentlikes.model.CommentLike;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findAllByCommentId(Long id);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM CommentLike cl
        WHERE cl.comment.id = :commentId AND cl.user.id = :userId
    """)
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
