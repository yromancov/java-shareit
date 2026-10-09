package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class NewUserRequest {
    private Long id;
    private String name;
    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Почта должна соответствовать формату.")
    private String email;


}
