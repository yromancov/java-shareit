package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.List;

public class ItemServiceImpl implements ItemService{
    @Override
    public List<ItemDto> getItems() {
        return List.of();
    }

    @Override
    public ItemDto getItem(long itemId) {
        return null;
    }

    @Override
    public ItemDto createItem(Long userId, NewItemRequest itemRequest) {
        return null;
    }

    @Override
    public ItemDto editItem(Long itemId) {
        return null;
    }

    @Override
    public ItemDto searchItem(String text) {
        return null;
    }
}
