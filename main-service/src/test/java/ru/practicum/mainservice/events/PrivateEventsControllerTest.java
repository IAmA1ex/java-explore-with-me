package ru.practicum.mainservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.mainservice.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.events.dto.NewEventDto;
import ru.practicum.mainservice.events.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.events.model.EventRequestStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getNewEventDto;
import static ru.practicum.mainservice.RandomStuff.getUpdateEventUserRequest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getEventsCreatedByUser() {
        try {
            mockMvc.perform(get("/users/{userId}/events", 100L))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createEvent() {
        try {
            NewEventDto newEventDto = getNewEventDto(1L, 1L, 1L);
            mockMvc.perform(post("/users/{userId}/events", 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newEventDto)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getEvent() {
        try {
            mockMvc.perform(get("/users/{userId}/events/{eventId}", 100L, 100L))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateEvent() {
        try {
            UpdateEventUserRequest updateEventUserRequest = getUpdateEventUserRequest(1L, 1L, 1L);
            mockMvc.perform(patch("/users/{userId}/events/{eventId}", 100L, 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateEventUserRequest)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getRequestsToUserEvent() {
        try {
            mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", 100L, 100L))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void handleRequestsToUserEvent() {
        try {
            EventRequestStatusUpdateRequest request = EventRequestStatusUpdateRequest.builder()
                    .requestIds(List.of(5L, 10L, 15L))
                    .status(EventRequestStatus.CONFIRMED)
                    .build();
            mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", 100L, 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("There is no such user."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}