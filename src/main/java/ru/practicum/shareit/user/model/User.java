package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    private String name;
    @NotBlank(message = "Почта не может быть пустой.")
    @Email(message = "Почта должна соответствовать формату.")
    private String email;

}
