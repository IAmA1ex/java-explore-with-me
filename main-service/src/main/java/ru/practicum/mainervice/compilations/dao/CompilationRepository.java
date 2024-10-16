package ru.practicum.mainervice.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainervice.compilations.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("""
        SELECT c FROM Compilation c
        WHERE c.pinned = :pinned
        ORDER BY c.id ASC
        LIMIT :size OFFSET :from
    """)
    List<Compilation> findAllByPinnedAndSorted(Boolean pinned, Long from, Long size);
}
