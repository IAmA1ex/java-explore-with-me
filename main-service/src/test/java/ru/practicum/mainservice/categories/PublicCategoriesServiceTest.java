package ru.practicum.mainservice.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.categories.service.PublicCategoriesService;
import ru.practicum.mainservice.exception.errors.NotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.mainservice.RandomStuff.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicCategoriesServiceTest {

    private PublicCategoriesService publicCategoriesService;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;

    private Map<Long, Category> categories;
    private Long primaryCategoryId;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = new CategoryMapper();
        publicCategoriesService = new PublicCategoriesService(categoryRepository, categoryMapper);

        categories = new HashMap<>();
        primaryCategoryId = 0L;

        for (long id = 0L; id <= 20L; id++) {
            categories.put(id, getCategory(id));
            primaryCategoryId = id;
        }

        when(categoryRepository.findAllCategoriesOrderById()).thenAnswer(arg -> {
            return categories.values().stream().sorted(Comparator.comparingLong(Category::getId)).toList();
        });

        when(categoryRepository.findAllFilter(anyLong(), anyLong())).thenAnswer(arg -> {
            Long from = arg.getArgument(0);
            Long size = arg.getArgument(1);
            return categories.values().stream().sorted(Comparator.comparingLong(Category::getId))
                    .skip(from).limit(size).toList();
        });

        when(categoryRepository.findById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            return Optional.ofNullable(categories.get(id));
        });
    }

    @Test
    void getCategories() {
        Long from = 4L;
        Long size = 5L;

        List<CategoryDto> categoryDtos = publicCategoriesService.getCategories(null, size);
        assertNotNull(categoryDtos);
        assertEquals(categories.size(), categoryDtos.size());

        categoryDtos = publicCategoriesService.getCategories(from, size);
        assertNotNull(categoryDtos);
        assertEquals(size, categoryDtos.size());
    }

    @Test
    void getCategoryTest() {
        Long id = primaryCategoryId;
        Long fakeId = primaryCategoryId + 1;

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                publicCategoriesService.getCategory(fakeId));
        assertEquals("There is no such category.", notFoundException.getMessage());
        assertEquals("Category with id = " + fakeId + " does not exist.", notFoundException.getReason());

        CategoryDto categoryDto = publicCategoriesService.getCategory(id);
        assertNotNull(categoryDto);
        assertEquals(id, categoryDto.getId());
    }
}