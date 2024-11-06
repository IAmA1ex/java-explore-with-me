package ru.practicum.mainservice.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.compilations.dao.CompilationEventRepository;
import ru.practicum.mainservice.compilations.dao.CompilationRepository;
import ru.practicum.mainservice.compilations.dto.CompilationDto;
import ru.practicum.mainservice.compilations.dto.CompilationMapper;
import ru.practicum.mainservice.compilations.model.Compilation;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.EventShortDto;
import ru.practicum.mainservice.events.model.Event;
import ru.practicum.mainservice.exception.errors.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, Long from, Long size) {
        List<Compilation> collections = compilationRepository.findAllByPinnedAndSorted(pinned, from, size);
        List<CompilationDto> compilationDtos = collections.stream()
                .map(compilationMapper::toCompilationDto)
                .peek(this::fillCompilationDto)
                .toList();
        log.debug("MAIN: {} compilations were found.", compilationDtos.size());
        return compilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {

        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation not found.",
                        "Compilation with id = " + compId + " does not exist."));
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        fillCompilationDto(compilationDto);
        log.debug("MAIN: {} was found.", compilationDto);
        return compilationDto;
    }

    private void fillCompilationDto(CompilationDto compilationDto) {
        List<Long> ids = compilationEventRepository.findAllIds(compilationDto.getId());
        List<Event> events = eventRepository.findAllById(ids);
        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
        compilationDto.setEvents(eventShortDtos);
    }
}
