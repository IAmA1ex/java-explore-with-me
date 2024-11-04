package ru.practicum.mainservice.participants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.events.model.EventRequestStatus;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.participants.dao.ParticipationRepository;
import ru.practicum.mainservice.participants.dto.ParticipationMapper;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;
import ru.practicum.mainservice.participants.model.Participant;
import ru.practicum.mainservice.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrivateParticipantsServiceTest {

    private PrivateParticipantsService privateParticipantsService;
    private ParticipationRepository participationRepository;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private ParticipationMapper participationMapper;

    private boolean userExistsById;
    private boolean eventExistsById;
    private boolean participationExistsByRequesterIdAndEventId;
    private boolean eventExistsByIdAndInitiatorId;
    private boolean eventExistsByIdAndState;
    private boolean isRequestModeration;
    private boolean participantExistsById;
    private boolean participationExistsByIdAndRequesterId;
    private boolean participationExistsByIdAndStatus;
    private Long countOfParticipants;

    @BeforeEach
    void setUp() {
        participationRepository = mock(ParticipationRepository.class);
        userRepository = mock(UserRepository.class);
        eventRepository = mock(EventRepository.class);
        participationMapper = new ParticipationMapper();
        privateParticipantsService = new PrivateParticipantsService(participationRepository, userRepository,
                eventRepository, participationMapper);

        userExistsById = false;
        eventExistsById = false;
        participantExistsById = false;
        participationExistsByRequesterIdAndEventId = true;
        participationExistsByIdAndRequesterId = false;
        participationExistsByIdAndStatus = true;
        eventExistsByIdAndInitiatorId = true;
        eventExistsByIdAndState = false;
        isRequestModeration = false;
        countOfParticipants = 0L;

        when(userRepository.existsById(anyLong())).thenAnswer(arg -> userExistsById);

        when(participationRepository.findAllByRequesterId(anyLong())).thenAnswer(arg -> {
            Long userId = arg.getArgument(0);
            List<Participant> participants = new ArrayList<>();
            for (long i = 1; i <= 5; i++) {
                Participant participant = getParticipant(i, 1L, userId);
                participants.add(participant);
            }
            return participants;
        });

        when(userRepository.findById(anyLong())).thenAnswer(arg -> {
            Long userId = arg.getArgument(0);
            if (userExistsById) return Optional.of(getUser(userId));
            return Optional.empty();
        });

        when(eventRepository.findById(anyLong())).thenAnswer(arg -> {
            Long eventId = arg.getArgument(0);
            Event event = getEvent(eventId, 1L, 1L);
            event.setRequestModeration(isRequestModeration);
            if (eventExistsById) return Optional.of(event);
            return Optional.empty();
        });

        when(participationRepository.existsByRequesterIdAndEventId(anyLong(), anyLong()))
                .thenAnswer(arg -> participationExistsByRequesterIdAndEventId);

        when(eventRepository.existsByIdAndInitiatorId(anyLong(), anyLong()))
                .thenAnswer(arg -> eventExistsByIdAndInitiatorId);

        when(eventRepository.existsByIdAndState(anyLong(), any()))
                .thenAnswer(arg -> eventExistsByIdAndState);

        when(eventRepository.countOfParticipants(anyLong()))
                .thenAnswer(arg -> countOfParticipants);

        when(participationRepository.save(any())).thenAnswer(arg -> {
            Participant participant = arg.getArgument(0);
            participant.setId(1L);
            return participant;
        });

        when(participationRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (participantExistsById) return Optional.of(getParticipant(id, 1L, 1L));
            return Optional.empty();
        });

        when(participationRepository.existsByIdAndRequesterId(anyLong(), anyLong())).thenAnswer(arg ->
                participationExistsByIdAndRequesterId);

        when(participationRepository.existsByIdAndStatus(anyLong(), any())).thenAnswer(arg ->
                participationExistsByIdAndStatus);
    }

    @Test
    void getUserRequests() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateParticipantsService.getUserRequests(1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        List<ParticipationRequestDto> requests = privateParticipantsService.getUserRequests(1L);
        assertNotNull(requests);
        assertEquals(5, requests.size());
    }

    @Test
    void createUserRequest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = 1 does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such event.", notFoundException.getMessage());
        assertEquals("Event with id = 1 does not exist.", notFoundException.getReason());

        eventExistsById = true;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Duplicate participation request.", conflictException.getMessage());
        assertEquals("User with id = 1 has already requested to participate in event with id = 1",
                conflictException.getReason());

        participationExistsByRequesterIdAndEventId = false;
        conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Event initiator cannot participate in own event.", conflictException.getMessage());
        assertEquals("User with id = 1 is the initiator of event with id = 1 and cannot request participation.",
                conflictException.getReason());

        eventExistsByIdAndInitiatorId = false;
        conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Cannot participate in an unpublished event.", conflictException.getMessage());
        assertEquals("Event with id = 1 is not in the 'PUBLISHED' state.", conflictException.getReason());

        eventExistsByIdAndState = true;
        countOfParticipants = 100L;
        conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.createUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Participation limit reached.", conflictException.getMessage());
        assertEquals("Event with id = 1 has reached the maximum number of participants.",
                conflictException.getReason());

        countOfParticipants = 0L;
        isRequestModeration = true;
        ParticipationRequestDto participationRequestDto = privateParticipantsService.createUserRequest(1L, 1L);
        assertNotNull(participationRequestDto);
        assertEquals(1L, participationRequestDto.getId());
        assertEquals(EventRequestStatus.PENDING, participationRequestDto.getStatus());

        isRequestModeration = false;
        participationRequestDto = privateParticipantsService.createUserRequest(1L, 1L);
        assertNotNull(participationRequestDto);
        assertEquals(1L, participationRequestDto.getId());
        assertEquals(EventRequestStatus.CONFIRMED, participationRequestDto.getStatus());
    }

    @Test
    void cancelUserRequest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                privateParticipantsService.cancelUserRequest(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + 1L + " does not exist.", notFoundException.getReason());

        userExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                privateParticipantsService.cancelUserRequest(1L, 1L));
        assertNotNull(notFoundException);
        assertEquals("There is no such request.", notFoundException.getMessage());
        assertEquals("Request with id = " + 1L + " does not exist.", notFoundException.getReason());

        participantExistsById = true;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.cancelUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Access denied.", conflictException.getMessage());
        assertEquals("User with id = " + 1L + " is not the owner of participation request with id = " +
                1L + ".", conflictException.getReason());

        participationExistsByIdAndRequesterId = true;
        conflictException = assertThrows(ConflictException.class, () ->
                privateParticipantsService.cancelUserRequest(1L, 1L));
        assertNotNull(conflictException);
        assertEquals("Request is already canceled.", conflictException.getMessage());
        assertEquals("Participation request with id = " + 1L + " has already been canceled.",
                conflictException.getReason());

        participationExistsByIdAndStatus = false;
        ParticipationRequestDto participationRequestDto = privateParticipantsService.cancelUserRequest(1L, 1L);
        assertNotNull(participationRequestDto);
        assertEquals(1L, participationRequestDto.getId());
        assertEquals(EventRequestStatus.CANCELED, participationRequestDto.getStatus());
    }
}