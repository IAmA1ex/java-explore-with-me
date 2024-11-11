package ru.practicum.mainservice.events.model;

import lombok.Getter;

@Getter
public enum EventsStates {

    PENDING(1L),
    PUBLISHED(2L),
    CANCELED(3L);

    private final Long id;

    EventsStates(Long id) {
        this.id = id;
    }

    public static EventsStates getById(Long id) {
        for (EventsStates status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }

    public Long getCode() {
        return id;
    }
}
