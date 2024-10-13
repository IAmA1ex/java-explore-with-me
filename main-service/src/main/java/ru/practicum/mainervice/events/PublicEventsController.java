package ru.practicum.mainervice.events;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.events.dto.EventFullDto;
import ru.practicum.mainervice.events.dto.EventShortDto;

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
            @RequestParam(required = false) @DefaultValue(value = "false") final Boolean onlyAvailable,
            @RequestParam(required = false) final String sort,
            @RequestParam(required = false) @DefaultValue(value = "0") final Long from,
            @RequestParam(required = false) @DefaultValue(value = "10") final Long size
    ) {
        return publicEventsService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable final Long id) {
        return publicEventsService.getEvent(id);
    }
}
