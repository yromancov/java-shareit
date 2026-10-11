package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;


import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getNextId());
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(itemStorage.get(itemId));
    }

    @Override
    public List<Item> getItemsByOwnerId(long ownerId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        String lower = text.toLowerCase();
        return itemStorage.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lower)
                        || item.getDescription().toLowerCase().contains(lower))
                .toList();
    }

    private long getNextId() {
        long currentMaxId = itemStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
