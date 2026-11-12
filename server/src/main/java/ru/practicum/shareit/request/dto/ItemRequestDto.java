package ru.practicum.shareit.request.dto;


import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
