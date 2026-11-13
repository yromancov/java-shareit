package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequests(Long userId, NewItemRequestDto dto);

    List<ItemRequestDto> getAllRequestsFromUser(Long userId);

    List<ItemRequestDto> getAllRequestsWithoutUser(Long userId);

    ItemRequestDto getRequestById(Long requestId);
}
