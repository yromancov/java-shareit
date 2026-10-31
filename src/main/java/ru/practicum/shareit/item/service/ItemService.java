package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import java.util.List;


public interface ItemService {
    ItemDto createItem(long userId, ItemDto dto);

    ItemDto editItem(long userId, long itemId, ItemDto dto);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByOwner(long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(long userId, long itemId, NewCommentDto dto);

}
