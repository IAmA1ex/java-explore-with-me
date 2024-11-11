package ru.practicum.mainservice.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.exception.errors.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCategoriesService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories(Long from, Long size) {
        List<Category> categories;
        if (from == null || size == null) categories = categoryRepository.findAllCategoriesOrderById();
        else categories = categoryRepository.findAllFilter(from, size);
        return categories.stream().map(categoryMapper::toCategoryDto).toList();
    }

    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("There is no such category.",
                    "Category with id = " + catId + " does not exist."));
        return categoryMapper.toCategoryDto(category);
    }
}
