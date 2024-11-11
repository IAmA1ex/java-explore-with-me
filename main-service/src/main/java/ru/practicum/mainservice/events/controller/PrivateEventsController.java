package ru.practicum.mainservice.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.events.service.PrivateEventsService;
import ru.practicum.mainservice.events.dto.*;
import ru.practicum.mainservice.participants.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {

    private final PrivateEventsService privateEventsService;

    @GetMapping
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") final Long from,
                                                      @RequestParam(defaultValue = "10") final Long size) {
        return privateEventsService.getEventsCreatedByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId,
                                    @RequestBody @Valid final NewEventDto newEventDto) {
        return privateEventsService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable("userId") Long userId,
                                 @PathVariable("eventId") Long eventId) {
        return privateEventsService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable("userId") Long userId,
                                    @PathVariable("eventId") Long eventId,
                                    @RequestBody @Valid final UpdateEventUserRequest updateEventUserRequest) {
        return privateEventsService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsToUserEvent(@PathVariable("userId") Long userId,
                                                                @PathVariable("eventId") Long eventId) {
        return privateEventsService.getRequestsToUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult handleRequestsToUserEvent(@PathVariable("userId") Long userId,
                                                                    @PathVariable("eventId") Long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest body) {
        return privateEventsService.handleRequestsToUserEvent(userId, eventId, body);
    }






}
