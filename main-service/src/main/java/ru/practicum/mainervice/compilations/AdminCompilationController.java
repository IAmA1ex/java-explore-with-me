package ru.practicum.mainervice.compilations;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.compilations.dto.CompilationDto;
import ru.practicum.mainervice.compilations.dto.NewCompilationDto;
import ru.practicum.mainervice.compilations.dto.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody @Valid final NewCompilationDto compilationDto) {
        return adminCompilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable final Long compId) {
        adminCompilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable final Long compId,
                                            @RequestBody final UpdateCompilationRequest compilationDto) {
        return adminCompilationService.updateCompilation(compId, compilationDto);
    }
}
