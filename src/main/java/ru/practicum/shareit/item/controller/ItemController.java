package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto){
        return itemService.createItem(userId,itemDto);
    }
    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ItemDto editItem(@PathVariable("itemId") long itemId){
        return itemService.editItem(itemId);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable("itemId") long itemId){
        return itemService.getItem(itemId);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems(){
        return itemService.getItems();
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto searchItem(@RequestParam("text") String text){
        return itemService.searchItem(text);
    }
}
