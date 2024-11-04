package ru.practicum.mainservice.events.dto.converters;

import org.junit.jupiter.api.Test;
import ru.practicum.mainservice.events.model.EventsStates;

import static org.junit.jupiter.api.Assertions.*;

class EventsStateConverterTest {

    private final EventsStateConverter converter = new EventsStateConverter();

    @Test
    void convertToDatabaseColumn() {
        EventsStates state = EventsStates.PUBLISHED;
        Long expectedCode = state.getCode();
        Long actualCode = converter.convertToDatabaseColumn(state);
        assertEquals(expectedCode, actualCode, "Код состояния должен совпадать с ожидаемым");
        assertNull(converter.convertToDatabaseColumn(null),
                "Для null состояния должно возвращаться null");
    }

    @Test
    void convertToEntityAttribute() {
        Long code = 1L;
        EventsStates expectedState = EventsStates.getById(code);
        EventsStates actualState = converter.convertToEntityAttribute(code);
        assertEquals(expectedState, actualState, "Состояние должно совпадать с ожидаемым");
        assertNull(converter.convertToEntityAttribute(null), "Для null кода должно возвращаться null");
    }
}