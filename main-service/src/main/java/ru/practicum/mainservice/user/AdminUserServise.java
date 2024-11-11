package ru.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.errors.ConflictException;
import ru.practicum.mainservice.exception.errors.NotFoundException;
import ru.practicum.mainservice.user.dao.UserRepository;
import ru.practicum.mainservice.user.dto.NewUserRequest;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.dto.UserMapper;
import ru.practicum.mainservice.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServise {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsers(List<Long> ids, Long from, Long size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.getUsersLimit(from, size);
        } else {
            users = userRepository.getSortedUsers(ids, from, size);
        }
        List<UserDto> userDtos = users.stream().map(userMapper::toUserDto).toList();
        log.debug("MAIN: {} users were received on request", userDtos.size());
        return userDtos;
    }

    public UserDto createUser(NewUserRequest newUserRequest) {

        if (userRepository.existsByEmail(newUserRequest.getEmail()))
            throw new ConflictException("Email conflict.",
                    "User with email " + newUserRequest.getEmail() + " already exists.");

        User user = userMapper.toUser(newUserRequest);
        user = userRepository.save(user);
        log.debug("MAIN: {} was created.", user);
        return userMapper.toUserDto(user);
    }


    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("There is no such user.",
                    "User with id = " + userId + " does not exist.");
        userRepository.deleteById(userId);
    }
}
