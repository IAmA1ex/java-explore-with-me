package ru.practicum.mainservice.participants;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateParticipantsController {

    private final PrivateParticipantsService privateParticipantsService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable final Long userId) {
        return privateParticipantsService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable final Long userId,
                                                     @RequestParam final Long eventId) {
        return privateParticipantsService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable final Long userId,
                                                     @PathVariable final Long requestId) {
        return privateParticipantsService.cancelUserRequest(userId, requestId);
    }
}
