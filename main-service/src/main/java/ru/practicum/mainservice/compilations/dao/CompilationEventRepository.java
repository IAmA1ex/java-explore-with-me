package ru.practicum.mainservice.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.compilations.model.CompilationEvent;

import java.util.List;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, Long> {

    void deleteAllByCompilationId(Long compilationId);

    @Query("""
        SELECT ce.event.id FROM CompilationEvent ce
        WHERE ce.compilation.id = :id
        ORDER BY ce.event.id ASC
    """)
    List<Long> findAllIds(Long id);
}
