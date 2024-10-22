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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getCategoryDto;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicCategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCategories() {
        try {
            CategoryDto categoryDto1 = getCategoryDto(1L);
            CategoryDto categoryDto2 = getCategoryDto(2L);
            CategoryDto categoryDto3 = getCategoryDto(3L);

            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto1)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto1.getName()));
            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto2)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto2.getName()));
            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto3)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto3.getName()));

            mockMvc.perform(get("/categories"))
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(categoryDto1.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(categoryDto2.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[2].name").value(categoryDto3.getName()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void getCategory() {
        try {
            CategoryDto categoryDto1 = getCategoryDto(1L);
            CategoryDto categoryDto2 = getCategoryDto(2L);

            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto1)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto1.getName()));
            mockMvc.perform(post("/admin/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto2)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto2.getName()));

            mockMvc.perform(get("/categories/1"))
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(categoryDto1.getName()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}