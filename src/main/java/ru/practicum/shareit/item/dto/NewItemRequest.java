package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class NewItemRequest {
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
}
