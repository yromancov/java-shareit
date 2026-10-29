package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;


public interface ItemService {
    ItemDto createItem(long userId, ItemDto dto);

    ItemDto editItem(long userId, long itemId, ItemDto dto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> searchItems(String text);


}
