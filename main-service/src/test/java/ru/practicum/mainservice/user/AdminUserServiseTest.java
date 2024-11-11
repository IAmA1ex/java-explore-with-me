package ru.practicum.mainservice.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.user.dao.UserRepository;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.dto.UserMapper;
import ru.practicum.mainservice.user.model.User;

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
class AdminUserServiseTest {

    private AdminUserServise adminUserServise;
    private UserRepository userRepository;
    private UserMapper userMapper;

    private Map<Long, User> users;
    private Long primaryUserId;
    private boolean existByEmail;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = new UserMapper();
        adminUserServise = new AdminUserServise(userRepository, userMapper);

        users = new HashMap<>();
        primaryUserId = 0L;
        existByEmail = false;

        for (long id = 0L; id <= 20L; id++) {
            users.put(id, getUser(id));
            primaryUserId = id;
        }

        when(userRepository.getUsersLimit(anyLong(), anyLong())).thenAnswer(arg -> {
            Long from = arg.getArgument(0);
            Long size = arg.getArgument(1);
            List<User> usersList = users.values().stream().sorted(Comparator.comparingLong(User::getId)).toList();
            return usersList.subList(from.intValue(), from.intValue() + size.intValue());
        });

        when(userRepository.getSortedUsers(anyList(), anyLong(), anyLong())).thenAnswer(arg -> {
            List<Long> ids = arg.getArgument(0);
            return ids.stream().map(i -> users.get(i)).toList();
        });

        when(userRepository.existsByEmail(anyString())).thenAnswer(arg -> {
            return existByEmail;
        });

        when(userRepository.save(any(User.class))).thenAnswer(arg -> {
            User user = arg.getArgument(0);
            primaryUserId++;
            user.setId(primaryUserId);
            users.put(primaryUserId, user);
            return user;
        });

        when(userRepository.existsById(anyLong())).thenAnswer(arg -> {
            Long id = arg.getArgument(0);
            return users.containsKey(id);
        });
    }

    @Test
    void getUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Long from = 5L;
        Long size = 7L;

        List<UserDto> users = adminUserServise.getUsers(List.of(), from, size);
        assertEquals(users.size(), size);

        List<UserDto> userDtos = adminUserServise.getUsers(ids, from, size);
        assertEquals(userDtos.size(), ids.size());
        assertTrue(userDtos.stream().allMatch(userDto -> ids.contains(userDto.getId())));
    }

    @Test
    void createUser() {
        NewUserRequest newUserRequest = getNewUserRequest(21L);

        existByEmail = true;
        ConflictException conflictException = assertThrows(ConflictException.class, () ->
                adminUserServise.createUser(newUserRequest));
        assertEquals("Email conflict.", conflictException.getMessage());
        assertEquals("User with email " + newUserRequest.getEmail() + " already exists.",
                conflictException.getReason());

        existByEmail = false;
        UserDto userDto = adminUserServise.createUser(newUserRequest);
        assertNotNull(userDto);
        assertEquals(21L, userDto.getId());
        assertEquals(newUserRequest.getEmail(), userDto.getEmail());
        assertEquals(newUserRequest.getName(), userDto.getName());

    }

    @Test
    void deleteUser() {
        Long fakeUserId = primaryUserId + 1;
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                adminUserServise.deleteUser(fakeUserId));
        assertEquals("There is no such user.", notFoundException.getMessage());
        assertEquals("User with id = " + fakeUserId + " does not exist.", notFoundException.getReason());

        assertDoesNotThrow(() -> adminUserServise.deleteUser(primaryUserId));
    }
}