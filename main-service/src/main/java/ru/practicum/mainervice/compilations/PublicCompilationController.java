package ru.practicum.mainervice.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.compilations.dto.CompilationDto;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(required = false) @DefaultValue(value = "0") Long from,
                                                @RequestParam(required = false) @DefaultValue(value = "10") Long size) {
        return publicCompilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return publicCompilationService.getCompilation(compId);
    }

}
