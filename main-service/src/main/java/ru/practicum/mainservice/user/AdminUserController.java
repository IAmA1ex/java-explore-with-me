package ru.practicum.mainservice.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserServise adminUserServise;

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) final List<Long> ids,
            @RequestParam(defaultValue = "0") final Long from,
            @RequestParam(defaultValue = "10") final Long size) {
        return adminUserServise.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid final NewUserRequest newUserRequest) {
        return adminUserServise.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        adminUserServise.deleteUser(userId);
    }
}
