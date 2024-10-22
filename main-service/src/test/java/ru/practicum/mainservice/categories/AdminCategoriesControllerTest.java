package ru.practicum.mainservice.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.mainservice.categories.dto.CategoryDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getCategoryDto;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCategory() {
        try {
            CategoryDto categoryDto = getCategoryDto(1L);

            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto.getName()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void deleteCategory() {
        try {
            CategoryDto categoryDto = getCategoryDto(1L);

            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto.getName()));

            mockMvc.perform(delete("/admin/categories/1"))
                    .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateCategory() {
        try {
            CategoryDto categoryDto = getCategoryDto(1L);
            CategoryDto categoryDtoUpdated = getCategoryDto(2L);
            categoryDtoUpdated.setName(categoryDtoUpdated.getName() + " updated");

            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto.getName()));

            mockMvc.perform(patch("/admin/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDtoUpdated)))
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(categoryDto.getId()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDtoUpdated.getName()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}