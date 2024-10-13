package ru.practicum.mainervice.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.categories.dao.CategoryRepository;
import ru.practicum.mainervice.categories.dto.CategoryDto;
import ru.practicum.mainervice.categories.dto.CategoryMapper;
import ru.practicum.mainervice.categories.model.Category;
import ru.practicum.mainervice.exception.errors.ConflictException;
import ru.practicum.mainervice.exception.errors.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCategoriesService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);
        category = categoryRepository.save(category);
        log.debug("MAIN: {} was created.", category);
        return categoryMapper.toCategoryDto(category);
    }

    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId))
            throw new NotFoundException("There is no such user.",
                    "Category with id = " + catId + " does not exist.");
        categoryRepository.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (!categoryRepository.existsById(catId))
            throw new NotFoundException("There is no such user.",
                    "Category with id = " + catId + " does not exist.");
        if (categoryRepository.existsByName(categoryDto.getName()))
            throw new ConflictException("Such a user already exists.",
                    "Category with name = " + categoryDto.getName() + " already exists.");
        Category category = categoryMapper.toCategory(categoryDto);
        category = categoryRepository.save(category);
        log.debug("MAIN: {} was updated.", category);
        return categoryMapper.toCategoryDto(category);
    }
}
