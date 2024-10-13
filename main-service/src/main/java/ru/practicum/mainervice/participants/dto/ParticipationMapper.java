package ru.practicum.mainervice.participants.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainervice.participants.model.Participant;

@Component
public class ParticipationMapper {

    public ParticipationRequestDto toParticipationRequestDto(Participant participant) {
        return ParticipationRequestDto.builder()
                .id(participant.getId())
                .event(participant.getEvent().getId())
                .requester(participant.getRequester().getId())
                .created(participant.getCreated())
                .status(participant.getStatus())
                .build();
    }
}
