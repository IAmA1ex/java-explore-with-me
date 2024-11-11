package ru.practicum.mainservice.compilations.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.compilations.model.Compilation;

@Component
public class CompilationMapper {

    public Compilation toCompilation(final NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public Compilation toCompilation(final UpdateCompilationRequest updateCompilationRequest) {
        return Compilation.builder()
                .pinned(updateCompilationRequest.getPinned())
                .title(updateCompilationRequest.getTitle())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
