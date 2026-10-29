package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setId(user.getId());
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        return user;
    }

    public static User updateUser(User user, UserDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }
        return user;
    }
}
