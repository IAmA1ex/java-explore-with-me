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
import ru.practicum.mainservice.events.dto.UpdateEventAdminRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
        UpdateEventAdminRequest updateEventAdminRequest = getUpdateEventAdminRequest(1L,
                1L, 1L);
        try {
            mockMvc.perform(patch("/admin/events/{eventId}", 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateEventAdminRequest)))
                    .andExpect(status().is4xxClientError());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}