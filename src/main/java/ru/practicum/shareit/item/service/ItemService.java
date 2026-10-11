package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;


public interface ItemService {
    ItemDto createItem(long userId, NewItemRequest request);

    ItemDto editItem(long userId, long itemId, UpdateItemRequest request);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> searchItems(String text);


}
