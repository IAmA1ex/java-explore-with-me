package ru.practicum.mainervice.events.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainervice.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long initiatorId);

    @Query("""
        SELECT e FROM Event e
        WHERE e.initiator.id = :userId
        ORDER BY e.id
        LIMIT :size OFFSET :from
    """)
    List<Event> findAllByInitiatorIdSorted(Long userId, Integer from, Integer size);

    @Query(value = """
            SELECT COUNT(*) FROM participants AS p
            LEFT JOIN participants_statuses AS ps ON p.status = ps.id
            WHERE p.event = :id AND ps.name = 'CONFIRMED'
            """, nativeQuery = true)
    Long countOfParticipants(Long id);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);
}
