package ru.practicum.mainervice.compilations.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainervice.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

}
