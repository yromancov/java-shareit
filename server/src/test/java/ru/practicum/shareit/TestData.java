package ru.practicum.shareit;

import java.time.LocalDateTime;

import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

public final class TestData {
    private TestData() {
    }

    public static UserDto user(String name) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(name + "@mail.ru");
        return dto;
    }

    public static ItemDto item(String name) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription("description " + name);
        dto.setAvailable(true);
        return dto;
    }

    public static NewBookingDto booking(long itemId, LocalDateTime start, LocalDateTime end) {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }
}