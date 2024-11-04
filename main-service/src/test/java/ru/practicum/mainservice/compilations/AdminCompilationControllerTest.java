package ru.practicum.mainservice.compilations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.mainservice.compilations.dto.UpdateCompilationRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.getNewCompilationDto;
import static ru.practicum.mainservice.RandomStuff.getUpdateCompilationRequest;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCompilation() {
        try {
            NewCompilationDto newCompilationDto = getNewCompilationDto();
            mockMvc.perform(post("/admin/compilations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newCompilationDto)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Some events were not found."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void deleteCompilation() {
        try {
            mockMvc.perform(delete("/admin/compilations/{compId}", 1L))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Compilation not found."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void updateCompilation() {
        try {
            UpdateCompilationRequest updateCompilationRequest = getUpdateCompilationRequest();
            mockMvc.perform(patch("/admin/compilations/{compId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateCompilationRequest)))
                    .andExpect(status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Compilation not found."));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}