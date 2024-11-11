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
import ru.practicum.mainservice.events.controller.AdminEventsController;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.service.AdminEventsService;
import ru.practicum.mainservice.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getEvent;
import static ru.practicum.mainservice.RandomStuff.getUpdateEventAdminRequest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EventMapper eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());

    @Mock
    private AdminEventsService adminEventsService;

    @InjectMocks
    private AdminEventsController adminEventsController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminEventsController).build();
    }

    @Test
    void getEvents() {
        try {
            mockMvc.perform(get("/admin/events"))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assertTrue(status < 500, "Expected status not to be 5xx, but was: " + status);
                    });
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateEvent() {
        try {
            when(adminEventsService.updateEvent(anyLong(), any())).thenAnswer(arg -> {
                Long id = arg.getArgument(0);
                Event event = getEvent(id, 1L, 1L);
                return eventMapper.toEventFullDto(event);
            });

            UpdateEventAdminRequest updateEventAdminRequest = getUpdateEventAdminRequest(1L,
                    1L, 1L);
            mockMvc.perform(patch("/admin/events/{eventId}", 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateEventAdminRequest)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}