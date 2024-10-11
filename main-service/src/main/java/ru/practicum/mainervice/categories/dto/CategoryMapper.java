package ru.practicum.mainervice.categories.dto;

import org.springframework.stereotype.Component;
import ru.practicum.mainervice.categories.model.Category;

@Component
public class CategoryMapper {

    public Category toCategory(final CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(final Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
