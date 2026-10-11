package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item createItem(Item item);

    Item update(Item item);

    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByOwnerId(long ownerId);

    List<Item> search(String text);
}
