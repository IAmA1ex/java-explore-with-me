package ru.practicum.mainservice.events.model;

import lombok.Getter;

@Getter
public enum EventRequestStatus {

    PENDING(1L),
    CONFIRMED(2L),
    REJECTED(3L),
    CANCELED(4L);

    private final Long id;

    EventRequestStatus(Long id) {
        this.id = id;
    }

    public static EventRequestStatus getById(Long id) {
        for (EventRequestStatus status : values()) {
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
