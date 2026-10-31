package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new ParameterNotValidException(dto.getEmail(), "email не может быть пустым");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateDataException("Этот email уже ипсользуется");
        }
        User user = UserMapper.toUser(dto);
        User saved = userRepository.save(user);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + userId + " не найден"));

    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + userId + " не найден"));
        if (dto.getEmail() != null
                && !dto.getEmail().equals(user.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateDataException("Этот email уже используется");
        }
        UserMapper.updateUser(user, dto);
        return UserMapper.toUserDto(userRepository.save(user));

    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
