package ru.practicum.mainervice.categories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainervice.categories.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    @Query("""
        SELECT c FROM Category c
        ORDER BY c.id ASC
        LIMIT :size OFFSET :from
    """)
    List<Category> findAllFilter(Integer from, Integer size);
}
