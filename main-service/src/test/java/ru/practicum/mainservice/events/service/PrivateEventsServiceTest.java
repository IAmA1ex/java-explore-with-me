package ru.practicum.mainservice.events.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.commentlikes.dao.CommentLikeRepository;
import ru.practicum.mainservice.commentlikes.dto.CommentLikesMapper;
import ru.practicum.mainservice.commentlikes.model.CommentLike;
import ru.practicum.mainservice.comments.dao.CommentRepository;
import ru.practicum.mainservice.comments.dto.CommentMapper;
import ru.practicum.mainservice.comments.dto.FullCommentDto;
import ru.practicum.mainservice.comments.dto.NewCommentDto;
import ru.practicum.mainservice.comments.dto.UpdateCommentDto;
import ru.practicum.mainservice.comments.model.Comment;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.participants.dao.ParticipationRepository;
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.participants.model.Participant;
import ru.practicum.mainservice.replies.dao.ReplyRepository;
import ru.practicum.mainservice.replies.dto.FullReplyDto;
import ru.practicum.mainservice.replies.dto.NewReplyDto;
import ru.practicum.mainservice.replies.dto.ReplyMapper;
import ru.practicum.mainservice.replies.dto.UpdateReplyDto;
import ru.practicum.mainservice.replies.model.Reply;
import ru.practicum.mainservice.replylikes.dao.ReplyLikeRepository;
import ru.practicum.mainservice.replylikes.dto.ReplyLikeMapper;
import ru.practicum.mainservice.replylikes.model.ReplyLike;
import ru.practicum.mainservice.user.dao.UserRepository;
import ru.practicum.mainservice.user.dto.UserMapper;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static ru.practicum.mainservice.RandomStuff.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateEventsServiceTest {

    private PrivateEventsService privateEventsService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private ParticipationRepository participationRepository;
    private CommentRepository commentRepository;
    private ReplyRepository replyRepository;
    private CommentLikeRepository commentLikeRepository;
    private ReplyLikeRepository replyLikeRepository;
    private EventMapper eventMapper;
    private CommentLikesMapper commentLikesMapper;
    private CommentMapper commentMapper;
    private ReplyMapper replyMapper;
    private ReplyLikeMapper replyLikeMapper;
    private ParticipationMapper participationMapper;
    private ServiceGeneralFunctionality sgf;
    private StatsGeneralFunctionality agf;

    private Map<String, Long> hits;
    private Map<Long, CommentLike> commentLikes;
    private Map<Long, ReplyLike> replyLikes;
    private boolean userExistsById;
    private boolean categoryExistById;
    private boolean eventExistById;
    private boolean commentLikeExist;
    private boolean replyLikeExist;
    private boolean isInitiator;
    private Long countOfParticipants;
    private Long eventId;
    private EventsStates eventState;
    private EventRequestStatus eventRequestStatus;
    private boolean commentExistById;
    private boolean replyExistById;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        participationRepository = mock(ParticipationRepository.class);
        commentRepository = mock(CommentRepository.class);
        replyRepository = mock(ReplyRepository.class);
        commentLikeRepository  = mock(CommentLikeRepository.class);
        replyLikeRepository = mock(ReplyLikeRepository.class);
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        commentLikesMapper = new CommentLikesMapper();
        commentMapper = new CommentMapper();
        replyMapper = new ReplyMapper();
        replyLikeMapper = new ReplyLikeMapper();
        participationMapper = new ParticipationMapper();
        StatsClient statsClient = mock(StatsClient.class);
        sgf = new ServiceGeneralFunctionality(eventRepository, commentRepository, categoryRepository,
                commentLikeRepository, replyRepository, replyLikeRepository, commentLikesMapper,
                replyMapper, replyLikeMapper);
        agf = new StatsGeneralFunctionality(statsClient);
        privateEventsService = new PrivateEventsService(eventRepository, userRepository, categoryRepository,
                participationRepository, commentRepository, commentLikeRepository, replyLikeRepository,
                replyRepository, eventMapper, commentMapper, replyMapper, participationMapper, sgf, agf);

        hits = new HashMap<>();
        commentLikes = new HashMap<>();
        replyLikes = new HashMap<>();
        userExistsById = false;
        categoryExistById = false;
        eventExistById = false;
        commentExistById = false;
        replyExistById = false;
        commentLikeExist = false;
        replyLikeExist = false;
        isInitiator = false;
        countOfParticipants = 0L;
        eventId = 1L;
        eventState = EventsStates.PUBLISHED;
        eventRequestStatus = EventRequestStatus.PENDING;

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

        when(userRepository.existsById(anyLong())).thenAnswer(arg -> userExistsById);

        when(eventRepository.findAllByInitiatorIdSorted(anyLong(), anyLong(), anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            List<Event> events = new ArrayList<>();
            for (long i = 1; i <= 5; i++) {
                events.add(getEvent(i, id, 1L));
            }
            return events;
        });

        when(eventRepository.countOfParticipants(anyLong())).thenAnswer(arg -> countOfParticipants);

        when(userRepository.findById(anyLong())).thenAnswer(arg -> {
            if (userExistsById) {
                Long id = arg.getArgument(0);
                return Optional.of(getUser(id));
            }
            return Optional.empty();
        });

        when(categoryRepository.findById(anyLong())).thenAnswer(arg -> {
            if (categoryExistById) {
                Long id = arg.getArgument(0);
                return Optional.of(getCategory(id));
            }
            return Optional.empty();
        });

        when(eventRepository.save(any(Event.class))).thenAnswer(arg -> {
            Event event = arg.getArgument(0);
            event.setId(eventId);
            eventId++;
            return event;
        });

        when(eventRepository.findById(anyLong())).thenAnswer(arg -> {
            if (eventExistById) {
                Long id = arg.getArgument(0);
                Event event = getEvent(id, 1L, 2L);
                event.setState(eventState);
                return Optional.of(event);
            }
            return Optional.empty();
        });

        when(eventRepository.existsById(anyLong())).thenAnswer(arg -> eventExistById);

        when(eventRepository.existsByIdAndInitiatorId(anyLong(), anyLong())).thenAnswer(arg -> {
            if (isInitiator) return true;
            return false;
        });

        when(participationRepository.findAllByEventIdAndStatus(anyLong(), any()))
                .thenAnswer(arg -> {
                    Long eventId = arg.getArgument(0);
                    EventRequestStatus status = arg.getArgument(1);
                    List<Participant> participants = new ArrayList<>();
                    for (long i = 1; i <= 5; i++) {
                        participants.add(getParticipant(i, eventId, 4096L + i + status.getId(), status));
                    }
                    return participants;
                });

        when(participationRepository.findAllById(anyList())).thenAnswer(arg -> {
            List<Long> participantIds = arg.getArgument(0);
            List<Participant> participants = new ArrayList<>();
            for (Long id : participantIds) {
                participants.add(getParticipant(id, eventId, 515L + id, eventRequestStatus));
            }
            return participants;
        });

        when(commentRepository.save(any())).thenAnswer(arg -> {
            Comment comment = arg.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        when(commentRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (commentExistById) return Optional.of(getComment(1L, 1L, 1L, 1L, 2L));
            return Optional.empty();
        });

        when(commentRepository.existsById(anyLong())).thenAnswer(arg -> commentExistById);

        Mockito.doNothing().when(commentRepository).deleteById(anyLong());

        when(replyRepository.save(any())).thenAnswer(arg -> {
            Reply reply = arg.getArgument(0);
            reply.setId(1L);
            return reply;
        });

        when(replyRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (replyExistById) return Optional.of(getReply(1L, 1L, 1L, 2L,
                    1L, 1L, 3L));
            return Optional.empty();
        });

        Mockito.doNothing().when(replyRepository).deleteById(anyLong());

        when(commentLikeRepository.save(any())).thenAnswer(arg -> {
            CommentLike commentLike = arg.getArgument(0);
            commentLike.setId(1L);
            commentLikes.put(1L, commentLike);
            return commentLike;
        });

        when(commentLikeRepository.findAllByCommentId(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            return commentLikes.values().stream()
                    .peek(cl -> cl.getComment().setId(id))
                    .toList();
        });

        when(replyRepository.findAllByCommentId(anyLong())).thenAnswer(arg -> List.of());

        when(commentLikeRepository.existsByCommentIdAndUserId(anyLong(), anyLong())).thenAnswer(arg ->
                commentLikeExist);

        Mockito.doNothing().when(commentLikeRepository).deleteByCommentIdAndUserId(anyLong(), anyLong());

        when(replyLikeRepository.save(any())).thenAnswer(arg -> {
            ReplyLike replyLike = arg.getArgument(0);
            replyLike.setId(1L);
            replyLikes.put(1L, replyLike);
            return replyLike;
        });

        when(replyLikeRepository.findAllByReplyId(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            return replyLikes.values().stream()
                    .peek(cl -> cl.getReply().setId(id))
                    .toList();
        });

        when(replyLikeRepository.existsByReplyIdAndUserId(anyLong(), anyLong())).thenAnswer(arg ->
                replyLikeExist);
    }

    @Test
    void getEventsCreatedByUser() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.getEventsCreatedByUser(1L, 0L, 10L));
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        List<EventShortDto> events = privateEventsService.getEventsCreatedByUser(1L, 0L, 10L);
        assertEquals(5, events.size());
        assertTrue(events.stream().anyMatch(event -> event.getInitiator().getId() == 1L));
        assertTrue(events.stream().anyMatch(event -> event.getViews() == 0L));
    }

    @Test
    void createEvent() {
        NewEventDto newEventDto1 = getNewEventDto(1L, 1L, 1L);
        newEventDto1.setEventDate(LocalDateTime.now().plusHours(1));

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.createEvent(1L, newEventDto1));
        assertNotNull(badRequestException);
        assertEquals("For the requested operation the conditions are not met.", badRequestException.getMessage());
        assertEquals(String.format("Event date must contain a date that has not yet occurred. Value: %s.",
                newEventDto1.getEventDate()), badRequestException.getReason());

        NewEventDto newEventDto2 = getNewEventDto(1L, 1L, 1L);
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createEvent(1L, newEventDto2));
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        NewEventDto newEventDto3 = getNewEventDto(1L, 1L, 1L);
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createEvent(1L, newEventDto3));
        assertEquals("There is no such category.", notFoundException.getMessage());
        assertEquals("Category with id = " + newEventDto3.getCategory() + " does not exist.",
                notFoundException.getReason());

        categoryExistById = true;
        countOfParticipants = 2L;
        hits.put("/events/1", 66L);
        NewEventDto newEventDto4 = getNewEventDto(1L, 1L, 1L);
        EventFullDto eventFullDto = privateEventsService.createEvent(1L, newEventDto4);
        assertNotNull(eventFullDto);
        assertEquals(1L, eventFullDto.getId());
        assertEquals(newEventDto4.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(newEventDto4.getCategory(), eventFullDto.getCategory().getId());
        assertEquals(countOfParticipants, eventFullDto.getConfirmedRequests());
        assertNotNull(eventFullDto.getCreatedOn());
        assertEquals(newEventDto4.getDescription(), eventFullDto.getDescription());
        assertEquals(newEventDto4.getEventDate(), eventFullDto.getEventDate());
        assertEquals(1L, eventFullDto.getInitiator().getId());
        assertEquals(newEventDto4.getLocation().getLat(), eventFullDto.getLocation().getLat());
        assertEquals(newEventDto4.getLocation().getLon(), eventFullDto.getLocation().getLon());
        assertEquals(newEventDto4.isPaid(), eventFullDto.isPaid());
        assertEquals(newEventDto4.getParticipantLimit(), eventFullDto.getParticipantLimit());
        assertNull(eventFullDto.getPublishedOn());
        assertEquals(newEventDto4.isRequestModeration(), eventFullDto.isRequestModeration());
        assertEquals(EventsStates.PENDING, eventFullDto.getState());
        assertEquals(newEventDto4.getTitle(), eventFullDto.getTitle());
        assertEquals(hits.get("/events/1"), eventFullDto.getViews());
    }

    @Test
    void getEventTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.getEvent(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.getEvent(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.getEvent(2L, 1L));
        assertNotNull(badRequestException);
        assertEquals("Access is denied.", badRequestException.getMessage());
        assertEquals("This user does not have access to this event.", badRequestException.getReason());

        countOfParticipants = 92L;
        hits.put("/events/1", 27L);
        EventFullDto eventFullDto = privateEventsService.getEvent(1L, 1L);
        assertEquals(1L, eventFullDto.getId());
        assertEquals(countOfParticipants, eventFullDto.getConfirmedRequests());
        assertEquals(1L, eventFullDto.getInitiator().getId());
        assertEquals(hits.get("/events/1"), eventFullDto.getViews());
    }

    @Test
    void updateEvent() {
        UpdateEventUserRequest request = getUpdateEventUserRequest(1L, 1L, 1L);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateEvent(1L, 1L, request));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateEvent(1L, 1L, request));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateEvent(2L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("User is not the initiator of the event.", badRequestException.getMessage());
        assertEquals("Only the event initiator can perform this action.", badRequestException.getReason());

        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                privateEventsService.updateEvent(1L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("Event state does not allow this action.", conflictException.getMessage());
        assertEquals("The action can only be performed on events in PENDING or CANCELED state.",
                conflictException.getReason());

        eventState = EventsStates.PENDING;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateEvent(1L, 1L, request));
        assertEquals("There is no such category.", notFoundException.getMessage());
        assertEquals("Category with id = " + request.getCategory() + " does not exist.",
                notFoundException.getReason());

        categoryExistById = true;
        request.setEventDate(LocalDateTime.now().plusHours(1));
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateEvent(1L, 1L, request));
        assertEquals("Event date is too soon.", badRequestException.getMessage());
        assertEquals("The event date must be at least 2 hours in the future.", badRequestException.getReason());

        request.setStateAction(EventsStatesAction.CANCEL_REVIEW);
        request.setEventDate(LocalDateTime.now().plusDays(1));
        EventFullDto eventFullDto = privateEventsService.updateEvent(1L, 1L, request);
        assertEquals(1L, eventFullDto.getId());
        assertEquals(EventsStates.CANCELED, eventFullDto.getState());

        countOfParticipants = 14L;
        hits.put("/events/2", 53L);
        request.setStateAction(EventsStatesAction.SEND_TO_REVIEW);
        eventFullDto = privateEventsService.updateEvent(1L, 1L, request);
        assertNotNull(eventFullDto);
        assertEquals(2L, eventFullDto.getId());
        assertEquals(request.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(request.getCategory(), eventFullDto.getCategory().getId());
        assertEquals(countOfParticipants, eventFullDto.getConfirmedRequests());
        assertNotNull(eventFullDto.getCreatedOn());
        assertEquals(request.getDescription(), eventFullDto.getDescription());
        assertEquals(request.getEventDate(), eventFullDto.getEventDate());
        assertEquals(1L, eventFullDto.getInitiator().getId());
        assertEquals(request.getLocation().getLat(), eventFullDto.getLocation().getLat());
        assertEquals(request.getLocation().getLon(), eventFullDto.getLocation().getLon());
        assertEquals(request.getPaid(), eventFullDto.isPaid());
        assertEquals(request.getParticipantLimit(), eventFullDto.getParticipantLimit());
        assertEquals(request.getRequestModeration(), eventFullDto.isRequestModeration());
        assertEquals(EventsStates.PENDING, eventFullDto.getState());
        assertEquals(request.getTitle(), eventFullDto.getTitle());
        assertEquals(hits.get("/events/2"), eventFullDto.getViews());
    }

    @Test
    void getRequestsToUserEvent() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.getRequestsToUserEvent(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.getRequestsToUserEvent(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.getRequestsToUserEvent(1L, 1L));
        assertNotNull(badRequestException);
        assertEquals("User is not the initiator of the event.", badRequestException.getMessage());
        assertEquals("Only the event initiator can perform this action.", badRequestException.getReason());

        isInitiator = true;
        List<ParticipationRequestDto> participationRequests = privateEventsService.getRequestsToUserEvent(1L, 1L);
        assertNotNull(participationRequests);
        assertEquals(5, participationRequests.size());
    }

    @Test
    void handleRequestsToUserEvent() {
        EventRequestStatusUpdateRequest request = getEventRequestStatusUpdateRequest(List.of(1L, 2L, 3L),
                EventRequestStatus.PENDING);

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("User is not the initiator of the event.", badRequestException.getMessage());
        assertEquals("Only the event initiator can perform this action.", badRequestException.getReason());

        isInitiator = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("Invalid request status.", badRequestException.getMessage());
        assertEquals("The status can only be set to CONFIRMED or REJECTED.", badRequestException.getReason());

        request.setStatus(EventRequestStatus.REJECTED);
        eventRequestStatus = EventRequestStatus.CONFIRMED;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("Invalid status change", conflictException.getMessage());
        assertEquals("Cannot change participant status to REJECTED.",
                conflictException.getReason());

        eventRequestStatus = EventRequestStatus.PENDING;
        EventRequestStatusUpdateResult result = privateEventsService
                .handleRequestsToUserEvent(1L, 1L, request);
        assertNotNull(result);
        assertEquals(result.getConfirmedRequests().size(), 5);
        assertTrue(result.getConfirmedRequests().stream()
                .allMatch(r -> r.getStatus().equals(EventRequestStatus.CONFIRMED)));
        assertEquals(result.getRejectedRequests().size(), 5);
        assertTrue(result.getRejectedRequests().stream()
                .allMatch(r -> r.getStatus().equals(EventRequestStatus.REJECTED)));

        request.setStatus(EventRequestStatus.CONFIRMED);
        countOfParticipants = 5L;
        conflictException = assertThrows(ConflictException.class, () ->
                privateEventsService.handleRequestsToUserEvent(1L, 1L, request));
        assertNotNull(badRequestException);
        assertEquals("Participation limit reached.", conflictException.getMessage());
        assertEquals("Event with id = " + 1L + " has reached the maximum number of participants.",
                conflictException.getReason());

        countOfParticipants = 0L;
        result = privateEventsService
                .handleRequestsToUserEvent(1L, 1L, request);
        assertNotNull(result);
        assertEquals(result.getConfirmedRequests().size(), 5);
        assertTrue(result.getConfirmedRequests().stream()
                .allMatch(r -> r.getStatus().equals(EventRequestStatus.CONFIRMED)));
        assertEquals(result.getRejectedRequests().size(), 5);
        assertTrue(result.getRejectedRequests().stream()
                .allMatch(r -> r.getStatus().equals(EventRequestStatus.REJECTED)));
    }

    @Test
    void createComment() {
        NewCommentDto newCommentDto = getNewCommentDto();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createComment(1L, 1L, newCommentDto));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createComment(1L, 1L, newCommentDto));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        FullCommentDto fullCommentDto = privateEventsService.createComment(1L, 1L, newCommentDto);
        assertNotNull(fullCommentDto);
        assertEquals(1L, fullCommentDto.getId());
        assertEquals(newCommentDto.getText(), fullCommentDto.getText());
    }

    @Test
    void updateComment() {
        UpdateCommentDto updateCommentDto = getUpdateCommentDto();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateComment(1L, 1L, 1L, updateCommentDto));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateComment(1L, 1L, 1L, updateCommentDto));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateComment(1L, 1L, 1L, updateCommentDto));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateComment(1L, 2L, 1L, updateCommentDto));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateComment(1L, 1L, 1L, updateCommentDto));
        assertEquals("The user cannot edit this comment.", badRequestException.getMessage());
        assertEquals("The user with id = " + 1L + " cannot edit the comment with id = " + 1L + ".",
                badRequestException.getReason());

        FullCommentDto fullCommentDto = privateEventsService.updateComment(2L, 1L, 1L, updateCommentDto);
        assertNotNull(fullCommentDto);
        assertEquals(1L, fullCommentDto.getId());
        assertEquals(updateCommentDto.getText(), fullCommentDto.getText());
    }

    @Test
    void deleteComment() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteComment(1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteComment(1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteComment(1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.deleteComment(1L, 2L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.deleteComment(1L, 1L, 1L));
        assertEquals("The user cannot edit this comment.", badRequestException.getMessage());
        assertEquals("The user with id = " + 1L + " cannot edit the comment with id = " + 1L + ".",
                badRequestException.getReason());

        assertDoesNotThrow(() -> privateEventsService.deleteComment(2L, 1L, 1L));
    }

    @Test
    void createReply() {
        NewReplyDto newReplyDto = getNewReplyDto();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createReply(1L, 1L, 1L, newReplyDto));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createReply(1L, 1L, 1L, newReplyDto));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.createReply(1L, 1L, 1L, newReplyDto));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.createReply(1L, 2L, 1L, newReplyDto));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        FullReplyDto fullReplyDto = privateEventsService.createReply(4L, 1L, 1L, newReplyDto);
        assertNotNull(fullReplyDto);
        assertEquals(1L, fullReplyDto.getId());
        assertEquals(newReplyDto.getText(), fullReplyDto.getText());
    }

    @Test
    void updateReply() {
        UpdateReplyDto updateReplyDto = getUpdateReplyDto();

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateReply(1L, 1L, 1L, 1L, updateReplyDto));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateReply(1L, 1L, 1L, 1L, updateReplyDto));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateReply(1L, 1L, 1L, 1L, updateReplyDto));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateReply(1L, 2L, 1L, 1L, updateReplyDto));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.updateReply(1L, 1L, 1L, 1L, updateReplyDto));
        assertEquals("There is no such reply.", notFoundException.getMessage());
        assertEquals("Reply with id = " + 1L + " does not exist.", notFoundException.getReason());

        replyExistById = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.updateReply(1L, 1L, 2L, 1L, updateReplyDto));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 2L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        FullReplyDto fullReplyDto = privateEventsService.updateReply(3L, 1L,
                1L, 1L, updateReplyDto);
        assertNotNull(fullReplyDto);
        assertEquals(1L, fullReplyDto.getId());
        assertEquals(updateReplyDto.getText(), fullReplyDto.getText());
    }

    @Test
    void deleteReply() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteReply(1L, 1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.deleteReply(1L, 2L, 1L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.deleteReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such reply.", notFoundException.getMessage());
        assertEquals("Reply with id = " + 1L + " does not exist.", notFoundException.getReason());

        replyExistById = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.deleteReply(1L, 1L, 2L, 1L));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 2L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        assertDoesNotThrow(() -> privateEventsService.deleteReply(3L, 1L,
                1L, 1L));
    }

    @Test
    void setLikeComment() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeComment(1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeComment(1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeComment(1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.setLikeComment(1L, 2L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        FullCommentDto fullCommentDto = privateEventsService.setLikeComment(1L, 1L, 1L);
        assertNotNull(fullCommentDto);
        assertEquals(1L, fullCommentDto.getId());
        assertFalse(fullCommentDto.getLikes().isEmpty());
        assertEquals(1L, fullCommentDto.getLikes().size());
    }

    @Test
    void removeLikeComment() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeComment(1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeComment(1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeComment(1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.removeLikeComment(1L, 2L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeComment(1L, 1L, 1L));
        assertEquals("There is no such like.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not set like to comment with id = " + 1L + ".",
                notFoundException.getReason());

        commentLikeExist = true;
        FullCommentDto fullCommentDto = privateEventsService.removeLikeComment(1L, 1L, 1L);
        assertNotNull(fullCommentDto);
        assertEquals(1L, fullCommentDto.getId());
        assertTrue(fullCommentDto.getLikes().isEmpty());
    }

    @Test
    void setLikeReply() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeReply(1L, 1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.setLikeReply(1L, 2L, 1L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.setLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such reply.", notFoundException.getMessage());
        assertEquals("Reply with id = " + 1L + " does not exist.", notFoundException.getReason());

        replyExistById = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.setLikeReply(1L, 1L, 2L, 1L));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 2L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        FullReplyDto fullReplyDto = privateEventsService.setLikeReply(1L, 1L, 1L, 1L);
        assertNotNull(fullReplyDto);
        assertEquals(1L, fullReplyDto.getId());
        assertFalse(fullReplyDto.getLikes().isEmpty());
        assertEquals(1L, fullReplyDto.getLikes().size());
    }

    @Test
    void removeLikeReply() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = " + 1L + " does not exist.", notFoundException.getReason());

        eventExistById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such comment.", notFoundException.getMessage());
        assertEquals("Comment with id = " + 1L + " does not exist.", notFoundException.getReason());

        commentExistById = true;
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.removeLikeReply(1L, 2L, 1L, 1L));
        assertEquals("The event does not contain such a comment.", badRequestException.getMessage());
        assertEquals("The event with id = " + 2L + " does not contain a comment with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such reply.", notFoundException.getMessage());
        assertEquals("Reply with id = " + 1L + " does not exist.", notFoundException.getReason());

        replyExistById = true;
        badRequestException = assertThrows(BadRequestException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 2L, 1L));
        assertEquals("The comment does not contain such a reply.", badRequestException.getMessage());
        assertEquals("The comment with id = " + 2L + " does not contain a reply with id = " + 1L + ".",
                badRequestException.getReason());

        notFoundException = assertThrows(NotFoundException.class, () ->
                privateEventsService.removeLikeReply(1L, 1L, 1L, 1L));
        assertEquals("There is no such like.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not set like to reply with id = " + 1L  + ".",
                notFoundException.getReason());

        replyLikeExist = true;
        FullReplyDto fullReplyDto = privateEventsService.removeLikeReply(1L, 1L, 1L, 1L);
        assertNotNull(fullReplyDto);
        assertEquals(1L, fullReplyDto.getId());
        assertTrue(fullReplyDto.getLikes().isEmpty());
    }
}