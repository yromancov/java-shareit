package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto dto) {
        return itemService.createItem(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") long userId,
                            @PathVariable("itemId") long itemId,
                            @RequestBody ItemDto dto) {
        return itemService.editItem(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable("itemId") long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItem(@RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable("itemId") long itemId,
                                 @Valid @RequestBody NewCommentDto dto) {
        return itemService.addComment(userId, itemId, dto);
    }
}
