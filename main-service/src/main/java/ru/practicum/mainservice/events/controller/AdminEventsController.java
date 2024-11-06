package ru.practicum.mainservice.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.events.service.AdminEventsService;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.events.model.EventsStates;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventsController {

    private final AdminEventsService adminEventsService;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<EventsStates> states,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") final Long from,
            @RequestParam(defaultValue = "10") final Long size) {
        return adminEventsService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable final Long eventId,
                                    @RequestBody @Valid final UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventsService.updateEvent(eventId, updateEventAdminRequest);
    }
}
