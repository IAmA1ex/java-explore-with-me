package ru.practicum.mainservice.replylikes.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.replylikes.model.ReplyLike;

import java.util.List;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {

    List<ReplyLike> findAllByReplyId(Long id);

    boolean existsByReplyIdAndUserId(Long replyId, Long userId);

    void deleteByReplyIdAndUserId(Long replyId, Long userId);
}
