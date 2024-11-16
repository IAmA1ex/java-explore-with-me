package ru.practicum.mainservice.events.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.service.PublicEventsService;
import ru.practicum.mainservice.user.dto.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PublicEventsService publicEventsService;

    @InjectMocks
    private PublicEventsController publicEventsController;

    private EventMapper eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicEventsController).build();
    }

    @Test
    void getEvents() {
        try {
            mockMvc.perform(get("/events"))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getEventTest() {
        try {
            when(publicEventsService.getEvent(anyLong(), any())).thenAnswer(arg -> {
                Long eventId = arg.getArgument(0);
                return eventMapper.toEventFullDto(getEvent(eventId, 1L, 1L));
            });

            mockMvc.perform(get("/events/{id}", 100))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                            .value(100));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getCommentsForEvent() {
        try {
            when(publicEventsService.getCommentsForEvent(anyLong())).thenAnswer(arg ->
                    List.of(getShortCommentDto(1L), getShortCommentDto(2L), getShortCommentDto(3L)));

            mockMvc.perform(get("/events/{eventId}/comments", 100))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()")
                            .value(3));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getComment() {
        try {
            when(publicEventsService.getComment(anyLong(), anyLong())).thenAnswer(arg ->
                    getFullCommentDto(1L));

            mockMvc.perform(get("/events/{eventId}/comments/{commentId}", 100, 100))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                            .value(1));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getReply() {
        try {
            when(publicEventsService.getReply(anyLong(), anyLong(), anyLong())).thenAnswer(arg ->
                    getFullReplyDto(1L));

            mockMvc.perform(get("/events/{eventId}/comments/{commentId}/replies/{replyId}",
                            100, 100, 100))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                            .value(1));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}