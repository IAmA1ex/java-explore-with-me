package ru.practicum.mainservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.events.controller.PrivateEventsController;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.events.service.PrivateEventsService;
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PrivateEventsService privateEventsService;

    @InjectMocks
    private PrivateEventsController privateEventsController;

    private EventMapper eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
    private ParticipationMapper participationMapper = new ParticipationMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateEventsController).build();
    }

    @Test
    void getEventsCreatedByUser() {
        try {
            when(privateEventsService.getEventsCreatedByUser(anyLong(), anyLong(), anyLong())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                List<EventShortDto> eventShortDto = new ArrayList<>();
                for (long i = 1; i <= 5; i++) {
                    eventShortDto.add(eventMapper.toEventShortDto(getEvent(i, userId, 1L)));
                }
                return eventShortDto;
            });

            mockMvc.perform(get("/users/{userId}/events", 100L))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].initiator.id").value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createEvent() {
        try {
            when(privateEventsService.createEvent(anyLong(), any())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                return eventMapper.toEventFullDto(getEvent(1L, userId, 1L));
            });

            NewEventDto newEventDto = getNewEventDto(1L, 1L, 1L);
            mockMvc.perform(post("/users/{userId}/events", 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newEventDto)))
                    .andExpect(status().is(201))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.initiator.id").value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getEventTest() {
        try {
            when(privateEventsService.getEvent(anyLong(), anyLong())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                Long eventId = arg.getArgument(1);
                return eventMapper.toEventFullDto(getEvent(eventId, userId, 1L));
            });

            mockMvc.perform(get("/users/{userId}/events/{eventId}", 100L, 200L))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(200))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.initiator.id").value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateEvent() {
        try {
            when(privateEventsService.updateEvent(anyLong(), anyLong(), any())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                Long eventId = arg.getArgument(1);
                return eventMapper.toEventFullDto(getEvent(eventId, userId, 1L));
            });

            UpdateEventUserRequest updateEventUserRequest = getUpdateEventUserRequest(1L, 1L, 1L);
            mockMvc.perform(patch("/users/{userId}/events/{eventId}", 100L, 200L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateEventUserRequest)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(200))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.initiator.id").value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getRequestsToUserEvent() {
        try {
            when(privateEventsService.getRequestsToUserEvent(anyLong(), anyLong())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                Long eventId = arg.getArgument(1);
                List<ParticipationRequestDto> participationRequestDto = new ArrayList<>();
                for (long i = 1; i <= 5; i++) {
                    participationRequestDto.add(participationMapper
                            .toParticipationRequestDto(getParticipant(i, eventId, userId)));
                }
                return participationRequestDto;
            });

            mockMvc.perform(get("/users/{userId}/events/{eventId}/requests", 100L, 200L))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].requester").value(100))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].event").value(200));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void handleRequestsToUserEvent() {
        try {
            when(privateEventsService.handleRequestsToUserEvent(anyLong(), anyLong(), any())).thenAnswer(arg ->
                    new EventRequestStatusUpdateResult(List.of(), List.of()));

            EventRequestStatusUpdateRequest request = EventRequestStatusUpdateRequest.builder()
                    .requestIds(List.of(5L, 10L, 15L, 20L))
                    .status(EventRequestStatus.CONFIRMED)
                    .build();
            mockMvc.perform(patch("/users/{userId}/events/{eventId}/requests", 100L, 200L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.confirmedRequests.size()").value(0))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.rejectedRequests.size()").value(0));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}