package ru.practicum.mainservice;

import ru.practicum.mainservice.categories.dto.CategoryDto;
import ru.practicum.mainservice.categories.model.Category;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.model.User;

public class RandomStuff {

    public static NewUserRequest getNewUserRequest(Long id) {
        return NewUserRequest.builder()
                .name("name" + id)
                .email("email" + id + "@email.com")
                .build();
    }

    public static User getUser(Long id) {
        return User.builder()
                .id(id)
                .name("name" + id)
                .email("email" + id + "@email.com")
                .build();
    }

    public static CategoryDto getCategoryDto(Long categoryId) {
        return CategoryDto.builder()
                .id(categoryId)
                .name("name" + categoryId)
                .build();
    }

    public static Category getCategory(Long categoryId) {
        return Category.builder()
                .id(categoryId)
                .name("name" + categoryId)
                .build();
    }
}
