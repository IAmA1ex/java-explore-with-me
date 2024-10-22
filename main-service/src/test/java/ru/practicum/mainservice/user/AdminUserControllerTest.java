package ru.practicum.mainservice.user;

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
import ru.practicum.mainservice.user.dto.NewUserRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.mainservice.RandomStuff.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUsers() {
        try {
            NewUserRequest userDto1 = getNewUserRequest(1L);
            NewUserRequest userDto2 = getNewUserRequest(2L);
            NewUserRequest userDto3 = getNewUserRequest(3L);

            mockMvc.perform(post("/admin/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto1)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto1.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto1.getEmail()));
            mockMvc.perform(post("/admin/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto2)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto2.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto2.getEmail()));
            mockMvc.perform(post("/admin/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto3)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto3.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto3.getEmail()));

            mockMvc.perform(get("/admin/users?ids=2,3"))
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(2))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(userDto2.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userDto2.getEmail()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(3))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(userDto3.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value(userDto3.getEmail()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createUser() {
        try {
            NewUserRequest userDto = getNewUserRequest(1L);

            mockMvc.perform(post("/admin/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void deleteUser() {
        try {
            NewUserRequest userDto = getNewUserRequest(1L);

            mockMvc.perform(post("/admin/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(status().is(HttpStatus.CREATED.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));

            mockMvc.perform(delete("/admin/users/1"))
                    .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}