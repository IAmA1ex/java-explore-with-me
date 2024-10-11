package ru.practicum.mainervice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainervice.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private AdminUserServise adminUserServise;

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) final List<String> ids,
            @RequestParam(required = false) final Integer from,
            @RequestParam(required = false) final Integer size) {
        return adminUserServise.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody final UserDto userDto) {
        return adminUserServise.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable final Long userId) {
        adminUserServise.deleteUser(userId);
    }
}
