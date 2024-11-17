package ru.practicum.mainservice.replies.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.replies.dto.ShortReplyDto;
import ru.practicum.mainservice.replies.model.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("""
        SELECT EXISTS (
            SELECT r FROM Reply r
            WHERE r.id = :replyId AND r.comment.id = :commentId
        )
    """)
    boolean isBelongsToComment(Long replyId, Long commentId);

    @Query("""
        SELECT new ru.practicum.mainservice.replies.dto.ShortReplyDto(
            r.id,
            r.createdOn,
            r.author.name,
            r.text,
            COUNT(DISTINCT rl.id)
        )
        FROM Reply r
        LEFT JOIN ReplyLike rl ON r.id = rl.reply.id
        WHERE r.comment.id = :commentId
        GROUP BY r.id, r.createdOn, r.author.name, r.text
        ORDER BY COUNT(DISTINCT rl.id) DESC
    """)
    List<ShortReplyDto> findAllByCommentId(Long commentId);
}
