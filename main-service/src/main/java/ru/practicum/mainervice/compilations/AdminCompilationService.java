package ru.practicum.mainervice.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainervice.compilations.dao.CompilationEventRepository;
import ru.practicum.mainervice.compilations.dto.CompilationDto;
import ru.practicum.mainervice.compilations.dto.CompilationMapper;
import ru.practicum.mainervice.compilations.dto.NewCompilationDto;
import ru.practicum.mainervice.compilations.dto.UpdateCompilationRequest;
import ru.practicum.mainervice.compilations.model.Compilation;
import ru.practicum.mainervice.compilations.dao.CompilationRepository;
import ru.practicum.mainervice.compilations.model.CompilationEvent;
import ru.practicum.mainervice.events.dao.EventRepository;
import ru.practicum.mainervice.events.dto.EventMapper;
import ru.practicum.mainervice.events.dto.EventShortDto;
import ru.practicum.mainervice.events.model.Event;
import ru.practicum.mainervice.exception.errors.NotFoundException;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        List<Event> events = checkEventsList(newCompilationDto.getEvents());

        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        compilation = compilationRepository.save(compilation);
        saveEventsForCompilation(compilation.getId(), newCompilationDto.getEvents());

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
        compilationDto.setEvents(eventShortDtos);
        log.info("MAIN: {} was created.", compilationDto);
        return compilationDto;
    }

    public void deleteCompilation(Long compId) {

        if (!compilationRepository.existsById(compId))
            throw new NotFoundException(
                    "Compilation not found.",
                    "Compilation with id = " + compId + " does not exist."
            );

        compilationRepository.deleteById(compId);
        log.info("MAIN: {}d Compilation has been deleted.", compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {

        if (!compilationRepository.existsById(compId))
            throw new NotFoundException(
                    "Compilation not found.",
                    "Compilation with id = " + compId + " does not exist."
            );

        List<Event> events = checkEventsList(updateCompilationRequest.getEvents());

        Compilation compilation = compilationMapper.toCompilation(updateCompilationRequest);
        compilation = compilationRepository.save(compilation);
        saveEventsForCompilation(compilation.getId(), updateCompilationRequest.getEvents());

        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
        compilationDto.setEvents(eventShortDtos);
        log.info("MAIN: {} was updated.", compilationDto);
        return compilationDto;

    }

    private List<Event> checkEventsList(List<Long> eventsIds) {
        List<Event> existsEvents = eventRepository.getExistingIdsFrom(eventsIds);
        List<Long> existsEventsIds = existsEvents.stream().map(Event::getId).toList();
        List<Long> nonExistingEventIds = eventsIds.stream()
                .filter(e -> !existsEventsIds.contains(e))
                .toList();
        if (!nonExistingEventIds.isEmpty())
            throw new NotFoundException(
                    "Some events were not found.",
                    "The following event IDs do not exist: " + nonExistingEventIds);
        return existsEvents;
    }

    private void saveEventsForCompilation(Long compilationId, List<Long> events) {
        compilationEventRepository.deleteAllByCompilationId(compilationId);
        List<CompilationEvent> compilationEvents = events.stream()
                .map(e -> CompilationEvent.builder()
                        .event(Event.builder()
                                .id(e)
                                .build())
                        .compilation(Compilation.builder()
                                .id(compilationId)
                                .build())
                        .build())
                .toList();
        compilationEventRepository.saveAll(compilationEvents);
    }
}
