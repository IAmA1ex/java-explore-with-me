package ru.practicum.mainervice.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.categories.dto.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {

    private final PublicCategoriesService publicCategoriesService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false) final Integer from,
                                           @RequestParam(required = false) final Integer size) {
        return publicCategoriesService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable final Long catId) {
        return publicCategoriesService.getCategory(catId);
    }
}
