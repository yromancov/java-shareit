package ru.practicum.shareit.user.service;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest userRequest);
    List<UserDto> getUsers();
    UserDto getUserById(long userId);
    UserDto updateUser(long userId, UpdateUserRequest userRequest);
    void deleteUser(long userId);
}
