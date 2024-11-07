package ru.practicum.statservice.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testHandleValidation() {
        BadRequestException badRequestException = new BadRequestException("validation message", "validation reason");
        ErrorResponse errorResponse = errorHandler.handleValidation(badRequestException);
        assertNotNull(errorResponse);
        assertEquals(badRequestException.getMessage(), errorResponse.getMessage());
        assertEquals(badRequestException.getReason(), errorResponse.getReason());
    }
}