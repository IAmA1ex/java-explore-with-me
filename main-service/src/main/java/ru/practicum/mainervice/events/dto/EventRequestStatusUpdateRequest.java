package ru.practicum.mainervice.events.dto;

import lombok.*;
import ru.practicum.mainervice.events.model.EventRequestStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private EventRequestStatus status;

}
