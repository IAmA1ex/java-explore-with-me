package ru.practicum.mainervice.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.categories.dao.CategoryRepository;
import ru.practicum.mainervice.categories.dto.CategoryDto;
import ru.practicum.mainervice.categories.dto.CategoryMapper;
import ru.practicum.mainervice.categories.model.Category;
import ru.practicum.mainervice.exception.errors.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCategoriesService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAllFilter(from, size);
        return categories.stream().map(categoryMapper::toCategoryDto).toList();
    }

    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElse(null);
        if (category == null) throw new NotFoundException("There is no such user.",
                    "Category with id = " + catId + " does not exist.");
        return categoryMapper.toCategoryDto(category);
    }
}
