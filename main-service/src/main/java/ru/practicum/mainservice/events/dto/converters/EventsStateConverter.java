package ru.practicum.mainservice.events.dto.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.practicum.mainservice.events.model.EventsStates;

@Converter(autoApply = true)
public class EventsStateConverter implements AttributeConverter<EventsStates, Long> {


    @Override
    public Long convertToDatabaseColumn(EventsStates eventsStates) {
        if (eventsStates == null) {
            return null;
        }
        return eventsStates.getCode();
    }

    @Override
    public EventsStates convertToEntityAttribute(Long aLong) {
        if (aLong == null) {
            return null;
        }
        return EventsStates.getById(aLong);
    }
}
