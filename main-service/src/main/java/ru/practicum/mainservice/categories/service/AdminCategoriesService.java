package ru.practicum.mainservice.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCategoriesService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto addCategory(CategoryDto categoryDto) {

        if (categoryRepository.existsByName(categoryDto.getName()))
            throw new ConflictException("Such a category already exists.",
                    "Category with name = " + categoryDto.getName() + " already exists.");

        Category category = categoryMapper.toCategory(categoryDto);
        category = categoryRepository.save(category);
        log.debug("MAIN: {} was created.", category);
        return categoryMapper.toCategoryDto(category);
    }

    public void deleteCategory(Long catId) {

        if (!categoryRepository.existsById(catId))
            throw new NotFoundException("There is no such category.",
                    "Category with id = " + catId + " does not exist.");

        if (categoryRepository.containsEvents(catId)) {
            throw new ConflictException("Category contains events.", "Cannot delete category with existing events.");
        }

        categoryRepository.deleteById(catId);
    }

    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {

        if (!categoryRepository.existsById(catId))
            throw new NotFoundException("There is no such category.",
                    "Category with id = " + catId + " does not exist.");

        Category byName = categoryRepository.findByName(categoryDto.getName());
        if (byName != null && !byName.getId().equals(catId))  // есть категория с таким же именем, но другой id
            throw new ConflictException("Such a category already exists.",
                    "Category with name = " + categoryDto.getName() + " already exists.");

        Category category = categoryMapper.toCategory(categoryDto);
        if (byName == null || !byName.getName().equals(category.getName())) {  // категория с таким именем не найдена
            category.setId(catId);
            category = categoryRepository.save(category);
        } else {  // категория с таким именем и id
            category.setId(catId);
        }
        log.debug("MAIN: {} was updated.", category);
        return categoryMapper.toCategoryDto(category);
    }
}
