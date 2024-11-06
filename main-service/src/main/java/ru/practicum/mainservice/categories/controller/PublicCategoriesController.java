package ru.practicum.mainservice.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.categories.service.PublicCategoriesService;
import ru.practicum.mainservice.categories.dto.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {

    private final PublicCategoriesService publicCategoriesService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") final Long from,
                                           @RequestParam(defaultValue = "10") final Long size) {
        return publicCategoriesService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable final Long catId) {
        return publicCategoriesService.getCategory(catId);
    }
}
