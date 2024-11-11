package ru.practicum.mainservice.events.dto.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.practicum.mainservice.events.model.EventRequestStatus;

@Converter(autoApply = true)
public class EventRequestStatusConverter implements AttributeConverter<EventRequestStatus, Long> {


    @Override
    public Long convertToDatabaseColumn(EventRequestStatus eventRequestStatus) {
        if (eventRequestStatus == null) {
            return null;
        }
        return eventRequestStatus.getCode();
    }

    @Override
    public EventRequestStatus convertToEntityAttribute(Long aLong) {
        if (aLong == null) {
            return null;
        }
        return EventRequestStatus.getById(aLong);
    }
}
