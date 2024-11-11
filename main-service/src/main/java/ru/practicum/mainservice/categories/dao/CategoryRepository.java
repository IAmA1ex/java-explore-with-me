package ru.practicum.mainservice.categories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainservice.categories.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
        SELECT c FROM Category c
        ORDER BY c.id ASC
        LIMIT :size OFFSET :from
    """)
    List<Category> findAllFilter(Long from, Long size);

    @Query("""
        SELECT c FROM Category c
        ORDER BY c.id ASC
    """)
    List<Category> findAllCategoriesOrderById();

    Category findByName(String name);

    boolean existsByName(String name);

    @Query("""
        SELECT COUNT(e) > 0 FROM Event e WHERE e.category.id = :category
    """)
    boolean containsEvents(Long category);
}
