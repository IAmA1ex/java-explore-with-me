package ru.practicum.mainservice.events.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.commentlikes.dao.CommentLikeRepository;
import ru.practicum.mainservice.commentlikes.dto.CommentLikesMapper;
import ru.practicum.mainservice.commentlikes.model.CommentLike;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.comments.dto.CommentMapper;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.dto.ShortCommentDto;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.StatsGeneralFunctionality;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.EventShortDto;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeMapper;
import ru.practicum.mainservice.replylikes.model.ReplyLike;
import ru.practicum.mainservice.user.dto.UserMapper;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicEventsServiceTest {

    private PublicEventsService publicEventsService;
    private EventRepository eventRepository;
    private CategoryRepository categoryRepository;
    private CommentRepository commentRepository;
    private EventMapper eventMapper;
    private CommentMapper commentMapper;
    private ReplyMapper replyMapper;
    private ReplyRepository replyRepository;
    private CommentLikeRepository commentLikeRepository;
    private ReplyLikeRepository replyLikeRepository;
    private CommentLikesMapper commentLikesMapper;
    private ReplyLikeMapper replyLikeMapper;
    private StatsClient statsClient;
    private ServiceGeneralFunctionality sgf;
    private StatsGeneralFunctionality agf;

    private Map<String, Long> hits;
    private Long categoriesDelta;
    private boolean eventExistById;
    private boolean commentExistById;
    private boolean replyExistById;
    private boolean isPublished;

    @BeforeEach
     void setUp() {
        eventRepository = mock(EventRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        commentRepository = mock(CommentRepository.class);
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        commentMapper = new CommentMapper();
        replyMapper = new ReplyMapper();
        replyRepository = mock(ReplyRepository.class);
        commentLikeRepository  = mock(CommentLikeRepository.class);
        replyLikeRepository = mock(ReplyLikeRepository.class);
        commentLikesMapper = new CommentLikesMapper();
        replyLikeMapper = new ReplyLikeMapper();
        statsClient = mock(StatsClient.class);
        sgf = new ServiceGeneralFunctionality(eventRepository, commentRepository, categoryRepository,
                commentLikeRepository, replyRepository, replyLikeRepository, commentLikesMapper,
                replyMapper, replyLikeMapper);
        agf = new StatsGeneralFunctionality(statsClient);
        publicEventsService = new PublicEventsService(eventRepository, categoryRepository, commentRepository,
                eventMapper, commentMapper, replyMapper, sgf, agf);

        hits = new HashMap<>();
        categoriesDelta = 0L;
        eventExistById = false;
        commentExistById = false;
        replyExistById = false;
        isPublished = false;

        when(statsClient.hit(any(NoteDto.class))).thenAnswer(arg -> {
            NoteDto noteDto = arg.getArgument(0);
            hits.replace(noteDto.getUri(), hits.getOrDefault(noteDto.getUri(), 0L) + 1);
            return null;
        });

        when(statsClient.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(List.class), anyBoolean()))
                .thenAnswer(arg -> {
                    List<String> statsUri = arg.getArgument(2);
                    List<StatDto> newStats = new ArrayList<>();
                    for (String s: statsUri) {
                        StatDto statDto = StatDto.builder()
                                .app("")
                                .uri(s)
                                .hits(hits.getOrDefault(s, 0L))
                                .build();
                        newStats.add(statDto);
                    }
                    return newStats;
                });

        when(categoryRepository.findAllById(anyList())).thenAnswer(arg -> {
            List<Long> categoryIds = arg.getArgument(0);
            List<Category> categories = new ArrayList<>();
            for (int i = 0; i < categoryIds.size() - categoriesDelta; i++) {
                categories.add(getCategory(categoryIds.get(i)));
            }
            return categories;
        });

        when(eventRepository.findAllByPublicFilters(any(), any(), any(), any(),
                any(), any(), anyLong(), anyLong())).thenAnswer(arg -> {
            List<Event> events = new ArrayList<>();
            for (long i = 1; i <= 5; i++) {
                events.add(getEvent(i, 1L, 1L));
                hits.put("/events/" + i, i + 100);
            }
            return events;
        });

        when(eventRepository.findById(anyLong())).thenAnswer(arg -> {
            if (eventExistById) {
                Long id = arg.getArgument(0);
                Event event = getEvent(id, 1L, 2L);
                if (isPublished) event.setPublishedOn(LocalDateTime.now().minusDays(2));
                else event.setPublishedOn(null);
                return Optional.of(event);
            }
            return Optional.empty();
        });

        when(commentRepository.findAllByEventId(anyLong())).thenAnswer(arg ->
                List.of(getShortCommentDto(1L), getShortCommentDto(2L)));

        when(eventRepository.existsById(anyLong())).thenAnswer(arg -> eventExistById);

        when(commentRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (commentExistById) return Optional.of(getComment(id, 1L, 2L, 1L, 1L));
            else return Optional.empty();
        });

        when(commentLikeRepository.findAllByCommentId(anyLong())).thenAnswer(arg -> {
            Long commentId = arg.getArgument(0);
            return List.of(
                    new CommentLike(1L, getComment(commentId, 1L, 2L, 1L, 1L), getUser(5L), LocalDateTime.now()),
                    new CommentLike(2L, getComment(commentId, 1L, 2L, 1L, 1L), getUser(6L), LocalDateTime.now())
            );
        });

        when(replyRepository.findAllByCommentId(anyLong())).thenAnswer(arg -> {
            Long commentId = arg.getArgument(0);
            return List.of(
                   getReply(1L, commentId, 1L, 1L, 1L, 1L, 7L),
                    getReply(1L, commentId, 1L, 1L, 1L, 1L, 8L)
            );
        });

        when(commentRepository.existsById(anyLong())).thenAnswer(arg -> commentExistById);

        when(replyRepository.findById(anyLong())).thenAnswer(arg -> {
            if (replyExistById) return Optional.of(getReply(1L, 1L, 1L, 1L, 1L, 1L, 7L));
            else return Optional.empty();
        });

        when(replyLikeRepository.findAllByReplyId(anyLong())).thenAnswer(arg -> {
            Long replyId = arg.getArgument(0);
            return List.of(
                    new ReplyLike(1L, getReply(replyId, 1L, 1L, 1L, 1L, 2L, 3L), getUser(4L), LocalDateTime.now()),
                    new ReplyLike(1L, getReply(replyId, 1L, 1L, 1L, 1L, 2L, 3L), getUser(5L), LocalDateTime.now())
            );
        });
    }

    @Test
    void getEvents() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getRemoteAddr()).thenReturn("11.12.13.14");
        when(httpServletRequest.getRequestURI()).thenReturn("/test/uri");

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> {
            publicEventsService.getEvents(null, List.of(-1L), null, null, null,
                    false, "EVENT_DATE", 0L, 10L, httpServletRequest);
        });
        assertNotNull(badRequestException);
        assertEquals("Invalid category ID.", badRequestException.getMessage());
        assertEquals("All category IDs must be greater than or equal to 1.", badRequestException.getReason());

        categoriesDelta = 2L;
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            publicEventsService.getEvents(null, List.of(1L, 2L, 3L, 4L), null, null, null,
                    false, "EVENT_DATE", 0L, 10L, httpServletRequest);
        });
        assertNotNull(notFoundException);
        assertEquals("One or more categories not found", notFoundException.getMessage());
        assertEquals("Some categories do not exist", notFoundException.getReason());

        categoriesDelta = 0L;
        List<EventShortDto> events = publicEventsService.getEvents(null, List.of(1L), null,
                null, null, false, "VIEWS", 0L, 10L, httpServletRequest);
        assertNotNull(events);
        assertEquals(5, events.size());
    }

    @Test
    void getEventTest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("10.10.10.10");
        when(request.getRequestURI()).thenReturn("/events/1");

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getEvent(1L, request));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getEvent(1L, request));
        assertNotNull(notFoundException);
        assertEquals("Event not published.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " has not been published yet.", notFoundException.getReason());

        isPublished = true;
        EventFullDto eventFullDto = publicEventsService.getEvent(1L, request);
        assertNotNull(eventFullDto);
        assertEquals(1L, eventFullDto.getId());
    }

    @Test
    void getCommentsForEvent() {
        List<ShortCommentDto> shortCommentDtos = publicEventsService.getCommentsForEvent(1L);
        assertNotNull(shortCommentDtos);
    }

    @Test
    void getCommentTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getComment(1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getComment(1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                publicEventsService.getComment(2L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        FullCommentDto fullCommentDto = publicEventsService.getComment(1L, 1L);
        assertNotNull(fullCommentDto);
        assertEquals(1L, fullCommentDto.getId());
        assertEquals(2, fullCommentDto.getLikes().size());
        assertEquals(2, fullCommentDto.getReplies().size());
    }

    @Test
    void getReplyTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getReply(1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getReply(1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                publicEventsService.getReply(2L, 1L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                publicEventsService.getReply(1L, 1L, 1L));
        assertEquals("There is no such reply.", notFoundException.getMessage());
        assertEquals("Reply with id = " + 1L + " does not exist.", notFoundException.getReason());

        replyExistById = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                publicEventsService.getReply(1L, 2L, 1L));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 2L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        FullReplyDto fullReplyDto = publicEventsService.getReply(1L, 1L, 1L);
        assertNotNull(fullReplyDto);
        assertEquals(1L, fullReplyDto.getId());
        assertEquals(2, fullReplyDto.getLikes().size());
    }
}