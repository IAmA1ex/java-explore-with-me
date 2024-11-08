package ru.practicum.mainservice.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.events.model.EventsStates;
import ru.practicum.mainservice.events.model.EventsStatesAction;
import ru.practicum.mainservice.events.service.PrivateEventsService;
import ru.practicum.mainservice.events.service.ServiceGeneralFunctionality;
import ru.practicum.mainservice.exception.errors.BadRequestException;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.participants.dao.ParticipationRepository;
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.participants.model.Participant;
import ru.practicum.mainservice.user.dao.UserRepository;
import ru.practicum.mainservice.user.dto.UserMapper;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.NoteDto;
import ru.practicum.statsdto.StatDto;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.mainservice.RandomStuff.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private EventMapper eventMapper;
    private ParticipationMapper participationMapper;
    private ServiceGeneralFunctionality sgf;
    private StatsGeneralFunctionality agf;

    private Map<String, Long> hits;
    private boolean userExistsById;
    private boolean categoryExistById;
    private boolean eventExistById;
    private boolean isInitiator;
    private Long countOfParticipants;
    private Long eventId;
    private EventsStates eventState;
    private EventRequestStatus eventRequestStatus;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        participationRepository = mock(ParticipationRepository.class);
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        participationMapper = new ParticipationMapper();
        StatsClient statsClient = mock(StatsClient.class);
        sgf = new ServiceGeneralFunctionality(categoryRepository);
        agf = new StatsGeneralFunctionality(eventRepository, statsClient);
        privateEventsService = new PrivateEventsService(eventRepository, userRepository, categoryRepository,
                participationRepository, eventMapper, participationMapper, sgf, agf);

        hits = new HashMap<>();
        userExistsById = false;
        categoryExistById = false;
        eventExistById = false;
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
}