package ru.practicum.mainservice.participants;

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
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getParticipant;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateParticipantsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ParticipationMapper participationMapper = new ParticipationMapper();

    @Mock
    private PrivateParticipantsService privateParticipantsService;

    @InjectMocks
    private PrivateParticipantsController privateParticipantsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(privateParticipantsController).build();
    }

    @Test
    void getUserRequests() {
        try {
            when(privateParticipantsService.getUserRequests(anyLong())).thenAnswer(arg -> {
                Long id = arg.getArgument(0);
                List<ParticipationRequestDto> participationRequestDtos = new ArrayList<>();
                for (long i = 1; i <= 5; i++) {
                    participationRequestDtos.add(participationMapper
                            .toParticipationRequestDto(getParticipant(i, i, id)));
                }
                return participationRequestDtos;
            });

            mockMvc.perform(get("/users/{userId}/requests", 1))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                            .value(5));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createUserRequest() {
        try {
            when(privateParticipantsService.createUserRequest(anyLong(), anyLong())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                Long eventId = arg.getArgument(1);
                return participationMapper.toParticipationRequestDto(getParticipant(1L, eventId, userId));
            });

            mockMvc.perform(post("/users/{userId}/requests", 3)
                            .param("eventId", "7"))
                    .andExpect(status().is(201))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.event").value(7))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.requester").value(3));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void cancelUserRequest() {
        try {
            when(privateParticipantsService.cancelUserRequest(anyLong(), anyLong())).thenAnswer(arg -> {
                Long userId = arg.getArgument(0);
                Long requestId = arg.getArgument(1);
                return participationMapper.toParticipationRequestDto(getParticipant(requestId, 1L, userId));
            });

            mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 8, 22))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(22))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.event").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.requester").value(8));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}