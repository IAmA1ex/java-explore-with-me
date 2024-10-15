package ru.practicum.mainervice.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainervice.compilations.model.CompilationEvent;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    void deleteAllByCompilationId(Long compilationId);
}
