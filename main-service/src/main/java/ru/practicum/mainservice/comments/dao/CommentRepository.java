package ru.practicum.mainservice.comments.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.comments.dto.ShortCommentDto;
import ru.practicum.mainservice.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT EXISTS (
            SELECT c FROM Comment c
            WHERE c.id = :commentId AND c.event.id = :eventId
        )
    """)
    boolean isBelongsToEvent(Long commentId, Long eventId);


    /*В данном запросе сортировка осуществляется по суммарному количеству лайков на комментарий и
    количеству лайков на ответы этого комментария*/
    @Query("""
        SELECT new ru.practicum.mainservice.comments.dto.ShortCommentDto(
            c.id,
            c.createdOn,
            c.author.name,
            c.text,
            COUNT(DISTINCT cl.id),
            COUNT(DISTINCT r.id)
        ) FROM Comment c
        LEFT JOIN CommentLike cl ON c.id = cl.comment.id
        LEFT JOIN Reply r ON c.id = r.comment.id
        LEFT JOIN ReplyLike rl ON r.id = rl.reply.id
        WHERE c.event.id = :eventId
        GROUP BY c.id, c.createdOn, c.author.name, c.text
        ORDER BY (COUNT(DISTINCT cl.id) + COUNT(DISTINCT rl.id)) DESC
    """)
    List<ShortCommentDto> findAllByEventId(Long eventId);
}
