package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems();

    ItemDto getItem(long itemId);

    ItemDto createItem(Long userId, NewItemRequest itemRequest);

    ItemDto editItem(Long itemId);

    ItemDto searchItem(String text);



}
