package ru.practicum.mainservice.events.service;

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
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.StatsGeneralFunctionality;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeMapper;
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
class AdminEventsServiceTest {

    private AdminEventsService adminEventsService;
    private EventRepository eventRepository;
    private CategoryRepository categoryRepository;
    private CommentRepository commentRepository;
    private ReplyRepository replyRepository;
    private CommentLikeRepository commentLikeRepository;
    private ReplyLikeRepository replyLikeRepository;
    private EventMapper eventMapper;
    private CommentLikesMapper commentLikesMapper;
    private ReplyMapper replyMapper;
    private ReplyLikeMapper replyLikeMapper;
    private ServiceGeneralFunctionality sgf;
    private StatsGeneralFunctionality agf;

    private Map<String, Long> hits;
    private List<Event> eventsToReturn;
    private Long countOfParticipants;
    private boolean eventExistById;
    private boolean commentExistById;
    private boolean replyIsBelongsToComment;
    private List<Category> categoriesToReturn;
    private boolean categoryFindById;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        commentRepository = mock(CommentRepository.class);
        replyRepository = mock(ReplyRepository.class);
        commentLikeRepository  = mock(CommentLikeRepository.class);
        replyLikeRepository = mock(ReplyLikeRepository.class);
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        commentLikesMapper = new CommentLikesMapper();
        replyMapper = new ReplyMapper();
        replyLikeMapper = new ReplyLikeMapper();
        StatsClient statsClient = mock(StatsClient.class);
        sgf = new ServiceGeneralFunctionality(eventRepository, commentRepository, categoryRepository,
                commentLikeRepository, replyRepository, replyLikeRepository, commentLikesMapper,
                replyMapper, replyLikeMapper);
        agf = new StatsGeneralFunctionality(statsClient);
        adminEventsService = new AdminEventsService(eventRepository, commentRepository, replyRepository,
                eventMapper, sgf, agf);

        hits = new HashMap<>();
        eventsToReturn = new ArrayList<>();
        categoriesToReturn = new ArrayList<>();
        countOfParticipants = 0L;
        eventExistById = false;
        commentExistById = false;
        categoryFindById = false;
        replyIsBelongsToComment = false;

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

        when(eventRepository.findAllByAdminFilters(any(), any(), any(), any(),
                any(), anyLong(), anyLong())).thenAnswer(arg -> eventsToReturn);

        when(eventRepository.countOfParticipants(anyLong())).thenAnswer(arg -> countOfParticipants);

        when(eventRepository.findById(anyLong())).thenAnswer(arg -> {
            if (eventExistById) {
                return Optional.of(eventsToReturn.getLast());
            }
            return Optional.empty();
        });

        when(categoryRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (categoryFindById) {
                return Optional.of(categoriesToReturn.getLast());
            }
            return Optional.empty();
        });

        when(eventRepository.save(any(Event.class))).thenAnswer(arg -> {
            Event event = arg.getArgument(0);
            event.setId(-1L);
            return event;
        });

        when(eventRepository.existsById(anyLong())).thenAnswer(arg -> eventExistById);

        when(commentRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (commentExistById) return Optional.of(getComment(1L, 1L, 1L, 1L, 2L));
            return Optional.empty();
        });

        when(replyRepository.isBelongsToComment(anyLong(), anyLong())).thenAnswer(arg -> replyIsBelongsToComment);

    }

    @Test
    void getEvents() {
        for (long i = 1L; i <= 5; i++) {
            eventsToReturn.add(getEvent(i, 1L, 1L));
            hits.put(String.format("/events/%d", i), i + 1);
        }

        countOfParticipants = 4L;
        List<EventFullDto> events = adminEventsService.getEvents(null, null,
                null, null, null, 0L, 10L);
        assertNotNull(events);
        assertEquals(5, events.size());
        assertTrue(events.stream().allMatch(e -> e.getConfirmedRequests().equals(countOfParticipants)));
        assertTrue(events.stream().allMatch(e -> e.getViews().equals(e.getId() + 1)));
    }

    @Test
    void updateEvent() {

        UpdateEventAdminRequest request = getUpdateEventAdminRequest(1L, 2L, 1L);
        Event event = getEvent(1L, 1L, 1L);
        eventsToReturn.add(event);
        categoriesToReturn.add(getCategory(2L));

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());

        eventExistById = true;
        request.setEventDate(LocalDateTime.now().plusHours(1));
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(badRequestException);
        assertEquals("Event date is too soon.", badRequestException.getMessage());
        request.setEventDate(LocalDateTime.now().plusDays(5));

        notFoundException = assertThrows(NotFoundException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(notFoundException);
        assertEquals("There is no such category.", notFoundException.getMessage());

        categoryFindById = true;
        event = getEvent(1L, 1L, 1L);
        eventsToReturn.add(event);
        ConflictException conflictException = assertThrows(ConflictException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(conflictException);
        assertEquals("Event cannot be published.", conflictException.getMessage());
        assertEquals("Only events in the PENDING state can be published. Current state: " + event.getState(),
                conflictException.getReason());

        event = getEvent(1L, 1L, 1L);
        event.setEventDate(LocalDateTime.now().plusMinutes(30));
        request.setEventDate(null);
        event.setState(EventsStates.PENDING);
        eventsToReturn.add(event);
        conflictException = assertThrows(ConflictException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(conflictException);
        assertEquals("Event cannot be published.", conflictException.getMessage());
        assertEquals("The event's date must be at least 1 hour in the future. Current event date: "
                        + event.getEventDate(), conflictException.getReason());

        event = getEvent(1L, 1L, 1L);
        request.setEventDate(null);
        event.setState(EventsStates.PENDING);
        eventsToReturn.add(event);
        EventFullDto eventFullDto = adminEventsService.updateEvent(1L, request);
        assertNotNull(eventFullDto);
        assertEquals(request.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(request.getCategory(), eventFullDto.getCategory().getId());
        assertEquals(request.getDescription(), eventFullDto.getDescription());
        assertEquals(event.getEventDate(), eventFullDto.getEventDate());
        assertEquals(-1L, eventFullDto.getId());
        assertEquals(request.getLocation().getLat(), eventFullDto.getLocation().getLat());
        assertEquals(request.getLocation().getLon(), eventFullDto.getLocation().getLon());
        assertEquals(request.getPaid(), eventFullDto.isPaid());
        assertEquals(request.getParticipantLimit(), eventFullDto.getParticipantLimit());
        assertEquals(request.getRequestModeration(), eventFullDto.isRequestModeration());
        assertEquals(event.getState(), EventsStates.PUBLISHED);
        assertEquals(event.getTitle(), eventFullDto.getTitle());
        assertEquals(0L, eventFullDto.getViews());

        event = getEvent(1L, 1L, 1L);
        event.setEventDate(LocalDateTime.now().plusMinutes(30));
        request.setEventDate(null);
        request.setStateAction(EventsStatesAction.REJECT_EVENT);
        eventsToReturn.add(event);
        conflictException = assertThrows(ConflictException.class, () -> {
            adminEventsService.updateEvent(1L, request);
        });
        assertNotNull(conflictException);
        assertEquals("Event cannot be rejected.", conflictException.getMessage());
        assertEquals("Published events cannot be rejected. Current state: " + event.getState(),
                conflictException.getReason());

        event = getEvent(1L, 1L, 1L);
        event.setEventDate(LocalDateTime.now().plusMinutes(30));
        event.setState(EventsStates.PENDING);
        request.setEventDate(null);
        request.setStateAction(EventsStatesAction.REJECT_EVENT);
        eventsToReturn.add(event);
        eventFullDto = adminEventsService.updateEvent(1L, request);
        assertNotNull(eventFullDto);
        assertEquals(request.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(request.getCategory(), eventFullDto.getCategory().getId());
        assertEquals(request.getDescription(), eventFullDto.getDescription());
        assertEquals(event.getEventDate(), eventFullDto.getEventDate());
        assertEquals(-1L, eventFullDto.getId());
        assertEquals(request.getLocation().getLat(), eventFullDto.getLocation().getLat());
        assertEquals(request.getLocation().getLon(), eventFullDto.getLocation().getLon());
        assertEquals(request.getPaid(), eventFullDto.isPaid());
        assertEquals(request.getParticipantLimit(), eventFullDto.getParticipantLimit());
        assertEquals(request.getRequestModeration(), eventFullDto.isRequestModeration());
        assertEquals(event.getState(), EventsStates.CANCELED);
        assertEquals(event.getTitle(), eventFullDto.getTitle());
        assertEquals(0L, eventFullDto.getViews());
    }

    @Test
    void deleteComment() {

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminEventsService.deleteComment(1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                adminEventsService.deleteComment(1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                adminEventsService.deleteComment(2L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        replyIsBelongsToComment = true;
        assertDoesNotThrow(() -> adminEventsService.deleteComment(1L, 1L));
    }

    @Test
    void deleteReply() {

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminEventsService.deleteReply(1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                adminEventsService.deleteReply(1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                adminEventsService.deleteReply(2L, 1L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        badRequestException = assertThrows(BadRequestException.class, () ->
                adminEventsService.deleteReply(1L, 1L, 1L));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 1L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        replyIsBelongsToComment = true;
        assertDoesNotThrow(() -> adminEventsService.deleteReply(1L, 1L, 1L));
    }
}