package ru.practicum.mainservice.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.compilations.dao.CompilationEventRepository;
import ru.practicum.mainservice.compilations.dao.CompilationRepository;
import ru.practicum.mainservice.compilations.dto.CompilationDto;
import ru.practicum.mainservice.compilations.dto.CompilationMapper;
import ru.practicum.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.mainservice.compilations.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilations.model.Compilation;
import ru.practicum.mainservice.compilations.service.AdminCompilationService;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.events.dto.EventShortDto;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static ru.practicum.mainservice.RandomStuff.*;

@Slf4j
@Service
@RequiredArgsConstructor
class AdminCompilationServiceTest {

    private CompilationRepository compilationRepository;
    private CompilationEventRepository compilationEventRepository;
    private EventRepository eventRepository;
    private CompilationMapper compilationMapper;
    private EventMapper eventMapper;
    private AdminCompilationService adminCompilationService;

    private boolean allEventsExists;
    private boolean compilationExistsById;

    @BeforeEach
    void setUp() {
        compilationRepository = mock(CompilationRepository.class);
        compilationEventRepository = mock(CompilationEventRepository.class);
        eventRepository = mock(EventRepository.class);
        compilationMapper = new CompilationMapper();
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        adminCompilationService = new AdminCompilationService(compilationRepository, compilationEventRepository,
                eventRepository, compilationMapper, eventMapper);

        allEventsExists = false;
        compilationExistsById = false;

        when(eventRepository.getExistingIdsFrom(anyList())).thenAnswer(arg -> {
            List<Long> ids = arg.getArgument(0);
            if (allEventsExists) return ids.stream().map(i -> getEvent(i, 1L, 1L)).toList();
            return List.of();
        });

        when(compilationRepository.save(any())).thenAnswer(arg -> {
            Compilation compilation = arg.getArgument(0);
            compilation.setId(1L);
            return compilation;
        });

        when(compilationRepository.existsById(anyLong())).thenAnswer(arg -> compilationExistsById);

        when(compilationRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (compilationExistsById) return Optional.of(getCompilation(id));
            else return Optional.empty();
        });
    }

    @Test
    void createCompilation() {
        NewCompilationDto newCompilationDto = getNewCompilationDto();
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminCompilationService.createCompilation(newCompilationDto));
        assertNotNull(notFoundException);
        assertEquals("Some events were not found.", notFoundException.getMessage());
        assertEquals("The following event IDs do not exist: " + newCompilationDto.getEvents(),
                notFoundException.getReason());

        allEventsExists = true;
        CompilationDto compilationDto = adminCompilationService.createCompilation(newCompilationDto);
        assertNotNull(compilationDto);
        assertEquals(newCompilationDto.getEvents(), compilationDto.getEvents().stream().map(EventShortDto::getId).toList());
        assertEquals(newCompilationDto.getPinned(), compilationDto.getPinned());
        assertEquals(newCompilationDto.getTitle(), compilationDto.getTitle());
    }

    @Test
    void deleteCompilation() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminCompilationService.deleteCompilation(1L));
        assertNotNull(notFoundException);
        assertEquals("Compilation not found.", notFoundException.getMessage());
        assertEquals("Compilation with id = " + 1L + " does not exist.", notFoundException.getReason());

        compilationExistsById = true;
        assertDoesNotThrow(() -> adminCompilationService.deleteCompilation(1L));
        verify(compilationRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateCompilation() {
        UpdateCompilationRequest updateCompilationRequest = getUpdateCompilationRequest();
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminCompilationService.updateCompilation(1L, updateCompilationRequest));
        assertNotNull(notFoundException);
        assertEquals("Compilation not found.", notFoundException.getMessage());
        assertEquals("Compilation with id = " + 1L + " does not exist.", notFoundException.getReason());

        compilationExistsById = true;
        notFoundException = assertThrows(NotFoundException.class, () ->
                adminCompilationService.updateCompilation(1L, updateCompilationRequest));
        assertNotNull(notFoundException);
        assertEquals("Some events were not found.", notFoundException.getMessage());
        assertEquals("The following event IDs do not exist: " + updateCompilationRequest.getEvents(),
                notFoundException.getReason());

        allEventsExists = true;
        CompilationDto compilationDto = adminCompilationService.updateCompilation(1L, updateCompilationRequest);
        assertNotNull(compilationDto);
        assertEquals(compilationDto.getEvents().stream().map(EventShortDto::getId).toList(),
                updateCompilationRequest.getEvents());
        assertEquals(compilationDto.getPinned(), updateCompilationRequest.getPinned());
        assertEquals(compilationDto.getTitle(), updateCompilationRequest.getTitle());
    }
}