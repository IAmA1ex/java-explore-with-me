package ru.practicum.mainervice.events.model;

import lombok.Getter;

@Getter
public enum EventsStatesAction {

    SEND_TO_REVIEW(1L),
    CANCEL_REVIEW(2L);

    private final Long id;

    EventsStatesAction(Long id) {
        this.id = id;
    }

    public static EventsStatesAction getById(Long id) {
        for (EventsStatesAction status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        return null;
    }
}
