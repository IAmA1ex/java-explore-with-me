package ru.practicum.mainservice.replylikes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.replylikes.model.ReplyLike;

import java.util.List;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    List<ReplyLike> findAllByReplyId(Long id);

    boolean existsByReplyIdAndUserId(Long replyId, Long userId);

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM ReplyLike rl
        WHERE rl.reply.id = :replyId AND rl.user.id = :userId
    """)
    void deleteByReplyIdAndUserId(Long replyId, Long userId);
}
