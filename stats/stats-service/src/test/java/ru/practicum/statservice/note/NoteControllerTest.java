package ru.practicum.statservice.note;

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
import ru.practicum.statsdto.NoteDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void hit() {
        try {
            NoteDto noteDto = NoteDto.builder()
                    .app("ewm-main-service")
                    .uri("/events/1")
                    .ip("192.163.0.1")
                    .timestamp(LocalDateTime.now())
                    .build();
            mockMvc.perform(post("/hit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(noteDto)))
                    .andExpect(status().is(201));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getStatsTrueAndArray() {
        try {
            Long unixTime = new Date().getTime();
            fill(unixTime);
            String uri = "/stats?start=1970-01-01 01:01:01&end=2050-01-01 01:01:01&unique=true" +
                    "&uris=/" + unixTime + "/1,/1234567/1,/" + unixTime + "/2,/1234567/2";
            mockMvc.perform(get(uri))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].app").value(unixTime))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].hits").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getStatsTrueAndNull() {
        try {
            Long unixTime = new Date().getTime();
            fill(unixTime);
            String uri = "/stats?start=1970-01-01 01:01:01&end=2050-01-01 01:01:01&unique=true";
            mockMvc.perform(get(uri))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].app").value(unixTime))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].hits").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getStatsFalseAndArray() {
        try {
            Long unixTime = new Date().getTime();
            fill(unixTime);
            String uri = "/stats?start=1970-01-01 01:01:01&end=2050-01-01 01:01:01&unique=false" +
                    "&uris=/" + unixTime + "/1,/1234567/1,/" + unixTime + "/2,/1234567/2";
            mockMvc.perform(get(uri))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].app").value(unixTime))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].hits").value(6))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getStatsFalseAndNull() {
        try {
            Long unixTime = new Date().getTime();
            fill(unixTime);
            String uri = "/stats?start=1970-01-01 01:01:01&end=2050-01-01 01:01:01&unique=false";
            mockMvc.perform(get(uri))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].app").value(unixTime))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].hits").value(6))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void fill(long currentTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 2; j++) {
                for (int k = 0; k < 3; k++) {
                    try {
                        NoteDto noteDto = NoteDto.builder()
                                .app(Long.toString(currentTime))
                                .uri("/" + currentTime + "/" + i)
                                .ip(String.format("255.%s.255.%d", currentTime, j))
                                .timestamp(localDateTime)
                                .build();
                        mockMvc.perform(post("/hit")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(noteDto)))
                                .andExpect(status().is(201));
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                }
            }
        }
    }
}