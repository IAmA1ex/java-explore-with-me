package ru.practicum.mainservice.events.dto.converters;

import org.junit.jupiter.api.Test;
import ru.practicum.mainservice.events.model.EventRequestStatus;

import static org.junit.jupiter.api.Assertions.*;

class EventRequestStatusConverterTest {

    private final EventRequestStatusConverter converter = new EventRequestStatusConverter();

    @Test
    void convertToDatabaseColumn() {
        EventRequestStatus status = EventRequestStatus.CONFIRMED;
        Long expectedCode = status.getCode();
        Long actualCode = converter.convertToDatabaseColumn(status);
        assertEquals(expectedCode, actualCode);
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute() {
        Long code = 1L;
        EventRequestStatus expectedStatus = EventRequestStatus.getById(code);
        EventRequestStatus actualStatus = converter.convertToEntityAttribute(code);
        assertEquals(expectedStatus, actualStatus);
        assertNull(converter.convertToEntityAttribute(null));
    }
}