package ru.practicum.mainervice.events.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.events.model.EventsStates;

import java.time.LocalDateTime;
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

    @Query("""
        SELECT e FROM Event e
        WHERE (:users IS NULL OR e.initiator.id IN :users) AND
            (:states IS NULL OR e.state IN :states) AND
            (:categories IS NULL OR e.category IN :categories) AND
            (:rangeStart IS NULL OR e.eventDate >= :rangeStart) AND
            (:rengeEnd IS NULL OR e.eventDate <= :rangeEnd)
        ORDER BY e.id
        LIMIT :size OFFSET :from
    """)
    List<Event> findAllByAdminFilters(List<Long> users,
                                      List<String> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Long from,
                                      Long size);
}
