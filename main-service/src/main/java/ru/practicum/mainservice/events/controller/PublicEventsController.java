package ru.practicum.mainservice.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.events.service.PublicEventsService;
import ru.practicum.mainservice.events.dto.EventFullDto;
import ru.practicum.mainservice.events.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final PublicEventsService publicEventsService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) final String text,
            @RequestParam(required = false) final List<Long> categories,
            @RequestParam(required = false) final Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            final LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") final Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") final String sort,
            @RequestParam(defaultValue = "0") final Long from,
            @RequestParam(defaultValue = "10") final Long size,
            HttpServletRequest request
    ) {
        return publicEventsService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable final Long id,
                                 HttpServletRequest request) {
        return publicEventsService.getEvent(id, request);
    }
}
