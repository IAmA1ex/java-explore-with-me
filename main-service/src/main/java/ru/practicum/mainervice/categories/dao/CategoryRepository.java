package ru.practicum.mainervice.categories.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainervice.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
