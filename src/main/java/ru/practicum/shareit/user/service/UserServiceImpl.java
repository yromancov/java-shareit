package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        User user = UserMapper.toUser(userRequest);
        User saved = userRepository.createUser(user);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.getUserById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + userId + " не найден"));

    }

    @Override
    public UserDto updateUser(long userId, UpdateUserRequest userRequest) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + userId + " не найден"));
        UserMapper.updateUserFields(user, userRequest);
        return UserMapper.toUserDto(userRepository.update(user));

    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }
}
