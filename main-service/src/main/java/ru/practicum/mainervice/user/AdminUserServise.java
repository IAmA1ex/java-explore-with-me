package ru.practicum.mainervice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainervice.exception.errors.NotFoundException;
import ru.practicum.mainervice.user.dao.UserRepository;
import ru.practicum.mainervice.user.dto.UserDto;
import ru.practicum.mainervice.user.dto.UserMapper;
import ru.practicum.mainervice.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServise {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (from == null || size == null) users = userRepository.findAllById(ids);
        else users = userRepository.getSortedUsers(ids, from, size);
        List<UserDto> userDtos = users.stream().map(userMapper::toUserDto).toList();
        log.debug("MAIN: {} users were received on request", userDtos.size());
        return userDtos;
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
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
