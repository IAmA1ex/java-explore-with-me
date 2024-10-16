package ru.practicum.mainservice.participants.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.participants.model.Participant;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByEventIdAndStatus(Long userId, EventRequestStatus eventRequestStatus);

    List<Participant> findAllByRequesterId(Long userId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    boolean existsByIdAndRequesterId(Long requestId, Long userId);

    boolean existsByIdAndStatus(Long requestId, EventRequestStatus status);
}
