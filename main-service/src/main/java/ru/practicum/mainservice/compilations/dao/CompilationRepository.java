package ru.practicum.mainservice.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.compilations.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("""
        SELECT c FROM Compilation c
        WHERE (:pinned IS NULL OR c.pinned = :pinned)
        ORDER BY c.id ASC
        LIMIT :size OFFSET :from
    """)
    List<Compilation> findAllByPinnedAndSorted(Boolean pinned, Long from, Long size);
}
