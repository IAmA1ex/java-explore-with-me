package ru.practicum.mainservice.events.model;

import lombok.Getter;

@Getter
public enum EventsStatesAction {

    SEND_TO_REVIEW(1L),
    CANCEL_REVIEW(2L),
    PUBLISH_EVENT(3L),
    REJECT_EVENT(4L);

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
