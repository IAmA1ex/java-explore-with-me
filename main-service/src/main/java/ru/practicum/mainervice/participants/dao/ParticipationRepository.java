package ru.practicum.mainervice.participants.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainervice.events.model.EventRequestStatus;
import ru.practicum.mainervice.participants.model.Participant;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByEventIdAndStatus(Long userId, EventRequestStatus eventRequestStatus);
}
