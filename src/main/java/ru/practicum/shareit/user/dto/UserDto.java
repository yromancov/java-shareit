package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Почта указана в неверном формате")
    private String email;
}
