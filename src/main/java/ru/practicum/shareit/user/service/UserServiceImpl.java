package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.item.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public UserDto createUser(NewUserRequest userRequest) {
    userRepository.createUser(userRequest);
    }

    @Override
    public List<UserDto> getUsers() {
        return List.of();
    }

    @Override
    public UserDto getUserById(long userId) {
        return null;
    }

    @Override
    public UserDto updateUser(long userId, UpdateUserRequest userRequest) {
        return null;
    }

    @Override
    public void deleteUser(long userId) {

    }
}
