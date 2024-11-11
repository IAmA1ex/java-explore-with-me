package ru.practicum.mainservice.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.categories.dao.CategoryRepository;
import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.dto.CategoryMapper;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.categories.service.AdminCategoriesService;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCategoriesServiceTest {

    private AdminCategoriesService adminCategoriesService;
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;

    private Map<Long, Category> categories;
    private Long primaryCategoryId;
    private Long needToFound;
    private boolean existById;
    private boolean existByName;
    private boolean containsEvents;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryMapper = new CategoryMapper();
        adminCategoriesService = new AdminCategoriesService(categoryRepository, categoryMapper);

        categories = new HashMap<>();
        primaryCategoryId = 0L;
        needToFound = 0L;
        existById = false;
        existByName = false;
        containsEvents = false;

        for (long id = 0L; id <= 20L; id++) {
            categories.put(id, getCategory(id));
            primaryCategoryId = id;
        }

        when(categoryRepository.existsByName(anyString())).thenAnswer(arg -> existByName);

        when(categoryRepository.save(any(Category.class))).thenAnswer(arg -> {
            Category category = arg.getArgument(0);
            if (category.getId() == null) {
                primaryCategoryId++;
                category.setId(primaryCategoryId);
                categories.put(category.getId(), category);
                return category;
            }
            categories.replace(category.getId(), category);
            return category;
        });

        when(categoryRepository.existsById(anyLong())).thenAnswer(arg -> existById);

        when(categoryRepository.containsEvents(anyLong())).thenAnswer(arg -> containsEvents);

        when(categoryRepository.findByName(anyString())).thenAnswer(arg -> {
            if (existByName) {
                Category category = getCategory(needToFound);
                category.setName(arg.getArgument(0));
                return category;
            } else {
                return null;
            }
        });

    }

    @Test
    void addCategory() {
        CategoryDto categoryDto = getCategoryDto(primaryCategoryId + 1);

        existByName = true;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                adminCategoriesService.addCategory(categoryDto));
        assertEquals("Such a category already exists.", conflictException.getMessage());
        assertEquals("Category with name = " + categoryDto.getName() + " already exists.",
                conflictException.getReason());

        existByName = false;
        CategoryDto categoryDtoCreated = adminCategoriesService.addCategory(categoryDto);
        assertNotNull(categoryDtoCreated);
        assertEquals(primaryCategoryId + 1, categoryDtoCreated.getId());
        assertEquals(categoryDto.getName(), categoryDtoCreated.getName());
    }

    @Test
    void deleteCategory() {
        Long id = 6L;

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminCategoriesService.deleteCategory(id));
        assertEquals("There is no such category.", notFoundException.getMessage());
        assertEquals("Category with id = " + id + " does not exist.", notFoundException.getReason());

        existById = true;
        containsEvents = true;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                adminCategoriesService.deleteCategory(id));
        assertEquals("Category contains events.", conflictException.getMessage());
        assertEquals("Cannot delete category with existing events.", conflictException.getReason());

        containsEvents = false;
        assertDoesNotThrow(() -> adminCategoriesService.deleteCategory(id));
    }

    @Test
    void updateCategory() {
        Long id = 6L;
        CategoryDto categoryDtoUpdate = new CategoryDto(null, "Updated name");

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminCategoriesService.updateCategory(id, categoryDtoUpdate));
        assertEquals("There is no such category.", notFoundException.getMessage());
        assertEquals("Category with id = " + id + " does not exist.", notFoundException.getReason());

        existById = true;
        existByName = true;
        needToFound = id + 1;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                adminCategoriesService.updateCategory(id, categoryDtoUpdate));
        assertEquals("Such a category already exists.", conflictException.getMessage());
        assertEquals("Category with name = " + categoryDtoUpdate.getName() + " already exists.",
                conflictException.getReason());

        needToFound = id;
        CategoryDto categoryUpdated = adminCategoriesService.updateCategory(id, categoryDtoUpdate);
        assertNotNull(categoryUpdated);
        assertEquals(id, categoryUpdated.getId());
        assertEquals(categoryDtoUpdate.getName(), categoryUpdated.getName());
        Mockito.verify(categoryRepository, Mockito.times(0)).save(any(Category.class));

        existByName = false;
        categoryUpdated = adminCategoriesService.updateCategory(id, categoryDtoUpdate);
        assertNotNull(categoryUpdated);
        assertEquals(id, categoryUpdated.getId());
        assertEquals(categoryDtoUpdate.getName(), categoryUpdated.getName());
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(Category.class));
    }
}