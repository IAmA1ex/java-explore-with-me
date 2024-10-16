package ru.practicum.mainservice.events.dto;

import lombok.*;
import ru.practicum.mainservice.events.model.EventRequestStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private EventRequestStatus status;

}
