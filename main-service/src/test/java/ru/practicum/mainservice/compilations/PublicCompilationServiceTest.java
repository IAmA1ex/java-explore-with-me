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
import ru.practicum.mainservice.compilations.model.Compilation;
import ru.practicum.mainservice.compilations.service.PublicCompilationService;
import ru.practicum.mainservice.events.dao.EventRepository;
import ru.practicum.mainservice.events.dto.EventMapper;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.mainservice.RandomStuff.getCompilation;

@Slf4j
@Service
@RequiredArgsConstructor
class PublicCompilationServiceTest {

    private CompilationRepository compilationRepository;
    private CompilationEventRepository compilationEventRepository;
    private EventRepository eventRepository;
    private CompilationMapper compilationMapper;
    private EventMapper eventMapper;
    private PublicCompilationService publicCompilationService;

    private boolean compilationExistsById;

    @BeforeEach
    void setUp() {
        compilationRepository = mock(CompilationRepository.class);
        compilationEventRepository = mock(CompilationEventRepository.class);
        eventRepository = mock(EventRepository.class);
        compilationMapper = new CompilationMapper();
        eventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        publicCompilationService = new PublicCompilationService(compilationRepository, compilationEventRepository,
                eventRepository, compilationMapper, eventMapper);

        compilationExistsById = false;

        when(compilationRepository.findAllByPinnedAndSorted(anyBoolean(), anyLong(), anyLong())).thenAnswer(arg -> {
            Boolean pinned = arg.getArgument(0);
            Long from = arg.getArgument(1);
            Long size = arg.getArgument(2);
            List<Compilation> compilations = new ArrayList<>();
            for (long id = 1L; id <= 5; id++) {
                Compilation compilation = getCompilation(id);
                compilation.setPinned(pinned);
                compilations.add(compilation);
            }
            return compilations;
        });

        when(compilationRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            if (compilationExistsById) return Optional.of(getCompilation(id));
            else return Optional.empty();
        });
    }

    @Test
    void getCompilations() {
        List<CompilationDto> compilationDtos = publicCompilationService.getCompilations(false, 0L, 10L);
        assertFalse(compilationDtos.isEmpty());
        assertTrue(compilationDtos.stream().allMatch(c -> c.getPinned().equals(false)));
    }

    @Test
    void getCompilationTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                publicCompilationService.getCompilation(1L));
        assertNotNull(notFoundException);
        assertEquals("Compilation not found.", notFoundException.getMessage());
        assertEquals("Compilation with id = " + 1L + " does not exist.", notFoundException.getReason());

        compilationExistsById = true;
        CompilationDto compilationDto = publicCompilationService.getCompilation(1L);
        assertNotNull(compilationDto);
        assertEquals(1L, compilationDto.getId());
    }
}