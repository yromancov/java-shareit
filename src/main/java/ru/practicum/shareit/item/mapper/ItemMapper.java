package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return dto;
    }
    public static Item toItem(NewItemRequest newItemRequest){
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setDescription(newItemRequest.getDescription());
        item.setAvailable(newItemRequest.isAvailable());
        item.setRequest(newItemRequest.getRequest());
        return item;
    }
}
