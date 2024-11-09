package ru.practicum.mainservice.events.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventsStates;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT e FROM Event e
        WHERE e.initiator.id = :userId
        ORDER BY e.id
        LIMIT :size OFFSET :from
    """)
    List<Event> findAllByInitiatorIdSorted(Long userId, Long from, Long size);

    @Query(value = """
            SELECT COUNT(*) FROM participants AS p
            LEFT JOIN participants_statuses AS ps ON p.status = ps.id
            WHERE p.event_id = :id AND ps.name = 'CONFIRMED'
            """, nativeQuery = true)
    Long countOfParticipants(Long id);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    @Query("""
        SELECT e FROM Event e
        WHERE (:users IS NULL OR e.initiator.id IN :users) AND
            (:states IS NULL OR e.state IN :states) AND
            (:categories IS NULL OR e.category.id IN :categories) AND
            (CAST(:rangeStart AS TIMESTAMP) IS NULL OR e.eventDate >= :rangeStart) AND
            (CAST(:rangeEnd AS TIMESTAMP) IS NULL OR e.eventDate <= :rangeEnd)
        ORDER BY e.id
        LIMIT :size OFFSET :from
    """)
    List<Event> findAllByAdminFilters(List<Long> users,
                                      List<EventsStates> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Long from,
                                      Long size);

    @Query("""
        SELECT e FROM Event e
        LEFT JOIN Participant AS p ON p.event.id = e.id
        WHERE ((:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', CAST(:text AS STRING), '%'))) OR
            (:text IS NULL OR LOWER(e.description) LIKE LOWER(CONCAT('%', CAST(:text AS STRING), '%')))) AND
            (:categories IS NULL OR e.category.id IN :categories) AND
            (:paid IS NULL OR e.paid IN :paid) AND
            (CAST(:rangeStart AS TIMESTAMP) IS NULL OR e.eventDate >= :rangeStart) AND
            (CAST(:rangeEnd AS TIMESTAMP) IS NULL OR e.eventDate <= :rangeEnd) AND
            (e.state = 2)
        GROUP BY e.id
        HAVING :onlyAvailable = false OR COUNT(p.id) < e.participantLimit
        ORDER BY e.eventDate DESC
        LIMIT :size OFFSET :from
    """)
    List<Event> findAllByPublicFilters(String text,
                                       List<Long> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       Long from,
                                       Long size);

    boolean existsByIdAndState(Long eventId, EventsStates state);

    @Query("""
        SELECT e FROM Event e
        WHERE e.id IN :events
    """)
    List<Event> getExistingIdsFrom(List<Long> events);
}
