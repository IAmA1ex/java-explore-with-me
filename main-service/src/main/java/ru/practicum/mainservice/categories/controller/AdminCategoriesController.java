package ru.practicum.mainservice.categories.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.categories.service.AdminCategoriesService;
import ru.practicum.mainservice.categories.dto.CategoryDto;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final AdminCategoriesService adminCategoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid final CategoryDto categoryDto) {
        return adminCategoriesService.addCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable final Long catId) {
        adminCategoriesService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable final Long catId,
                                      @RequestBody @Valid final CategoryDto categoryDto) {
        return adminCategoriesService.updateCategory(catId, categoryDto);
    }
}
