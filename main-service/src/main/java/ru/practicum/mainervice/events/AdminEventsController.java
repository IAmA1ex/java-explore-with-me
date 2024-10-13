package ru.practicum.mainervice.events;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.events.dto.EventFullDto;
import ru.practicum.mainervice.events.dto.UpdateEventAdminRequest;
import ru.practicum.mainervice.events.model.EventsStates;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventsController {

    private final AdminEventsSevice adminEventsSevice;

    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) final List<Long> users,
            @RequestParam(required = false) final List<EventsStates> states,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeEnd,
            @RequestParam(required = false) @DefaultValue(value = "0") final Long from,
            @RequestParam(required = false) @DefaultValue(value = "10") final Long size) {
        return adminEventsSevice.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable final Long eventId,
                                    @RequestBody final UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventsSevice.updateEvent(eventId, updateEventAdminRequest);
    }
}
