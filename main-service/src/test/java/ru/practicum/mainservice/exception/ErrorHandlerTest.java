package ru.practicum.mainservice.exception;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.mainservice.exception.errors.*;
import ru.practicum.mainservice.user.dto.NewUserRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound() {
        NotFoundException notFoundException = new NotFoundException("not found message", "not found reason");
        ErrorResponse errorResponse = errorHandler.handleNotFound(notFoundException);
        assertNotNull(errorResponse);
        assertEquals(notFoundException.getMessage(), errorResponse.getMessage());
        assertEquals(notFoundException.getReason(), errorResponse.getReason());
    }

    @Test
    void handleConflict() {
        ConflictException conflictException = new ConflictException("conflict message", "conflict reason");
        ErrorResponse errorResponse = errorHandler.handleConflict(conflictException);
        assertNotNull(errorResponse);
        assertEquals(conflictException.getMessage(), errorResponse.getMessage());
        assertEquals(conflictException.getReason(), errorResponse.getReason());
    }

    @Test
    void handleValidation() {
        ValidationException validationException = new ValidationException("validation message", "validation reason");
        ErrorResponse errorResponse = errorHandler.handleValidation(validationException);
        assertNotNull(errorResponse);
        assertEquals(validationException.getMessage(), errorResponse.getMessage());
        assertEquals(validationException.getReason(), errorResponse.getReason());
    }

    @Test
    void testHandleValidation() {
        BadRequestException badRequestException = new BadRequestException("validation message", "validation reason");
        ErrorResponse errorResponse = errorHandler.handleValidation(badRequestException);
        assertNotNull(errorResponse);
        assertEquals(badRequestException.getMessage(), errorResponse.getMessage());
        assertEquals(badRequestException.getReason(), errorResponse.getReason());
    }

    @Test
    void handleForbidden() {
        ForbiddenException forbiddenException = new ForbiddenException("forbidden message", "forbidden reason");
        ErrorResponse errorResponse = errorHandler.handleForbidden(forbiddenException);
        assertNotNull(errorResponse);
        assertEquals(forbiddenException.getMessage(), errorResponse.getMessage());
        assertEquals(forbiddenException.getReason(), errorResponse.getReason());
    }

    @Test
    void testHandleForbidden() {
        String invalidValue = "notAnInteger";
        try {
            int value = Integer.parseInt(invalidValue);
        } catch (NumberFormatException e) {
            Method method = getClass().getMethods()[0];
            MethodParameter methodParameter = new MethodParameter(method, 0);
            MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(invalidValue,
                    Integer.class, "name", methodParameter, e);
            ErrorResponse errorResponse = errorHandler.handleForbidden(exception);
            assertNotNull(errorResponse);
            assertEquals("Error converting values.", errorResponse.getMessage());
            assertEquals("Failed to convert value of type 'java.lang.String' to required type " +
                    "'java.lang.Integer'; For input string: \"notAnInteger\"", errorResponse.getReason());
        }
    }

    @Test
    void testHandleForbidden1() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("param", "String");
        ErrorResponse errorResponse = errorHandler.handleForbidden(exception);
        assertNotNull(errorResponse);
        assertEquals("Could not resolve parameter.", errorResponse.getMessage());
        assertEquals("Required request parameter 'param' for method parameter type String is not present",
                errorResponse.getReason());
    }

    @Test
    void testHandleValidation1() {
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            NewUserRequest invalidUserRequest = NewUserRequest.builder()
                    .name("User name")
                    .email("invalid-email")
                    .build();
            BindingResult bindingResult = new BeanPropertyBindingResult(invalidUserRequest, "newUserRequest");
            validator.validate(invalidUserRequest).forEach(violation ->
                    bindingResult.addError(new FieldError("newUserRequest",
                            violation.getPropertyPath().toString(),
                            violation.getMessage()))
            );
            MethodParameter methodParameter = new MethodParameter(
                    ErrorHandlerTest.class.getDeclaredMethod("testHandleValidation1"), -1);

            MethodArgumentNotValidException exception =
                    new MethodArgumentNotValidException(methodParameter, bindingResult);
            ErrorResponse errorResponse = errorHandler.handleValidation(exception);
            assertNotNull(errorResponse);
            assertEquals("Validation exception.", errorResponse.getMessage());
            assertTrue(errorResponse.getReason().contains("Email is not correct."));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    void handleRuntimeException() {
        RuntimeException runtimeException = new RuntimeException("runtime message");
        ErrorResponse errorResponse = errorHandler.handleRuntimeException(runtimeException);
        assertNotNull(errorResponse);
        assertEquals("Internal server error.", errorResponse.getMessage());
        assertEquals(runtimeException.getMessage(), errorResponse.getReason());
    }
}