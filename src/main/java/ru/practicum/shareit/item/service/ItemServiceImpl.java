package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        ItemDto dto = ItemMapper.toItemDto(item);
        dto.setComments(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList());
        if (item.getOwner().getId().equals(userId)) {
            fillBookings(dto,
                    bookingRepository.findAllByItemIdAndStatusOrderByStartAsc(itemId, Status.APPROVED),
                    LocalDateTime.now());
        }
        return dto;
    }

    @Override
    public List<ItemDto> getItemsByOwner(long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Booking>> bookingsByItem = bookingRepository
                .findAllByItemIdInAndStatusOrderByStartAsc(itemIds, Status.APPROVED).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<CommentDto>> commentsByItem = commentRepository
                .findAllByItemIdInOrderByCreatedDesc(itemIds).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())));
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    ItemDto dto = ItemMapper.toItemDto(item);
                    dto.setComments(commentsByItem.getOrDefault(item.getId(), List.of()));
                    fillBookings(dto, bookingsByItem.getOrDefault(item.getId(), List.of()), now);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto dto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ParameterNotValidException("name", "не может быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ParameterNotValidException("description", "не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ParameterNotValidException("available", "должен быть указан");
        }
        Item item = ItemMapper.toItem(dto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto editItem(long userId, long itemId, ItemDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только владелец");
        }
        ItemMapper.updateItemFields(item, dto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, NewCommentDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь " + itemId + " не найдена"));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Автор не найден"));
        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                author.getId(),
                itemId,
                Status.APPROVED,
                LocalDateTime.now())) {
            throw new ParameterNotValidException("itemId", "пользователь не брал эту вещь в аренду");
        }
        Comment comment = CommentMapper.toComment(dto, item, author);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void fillBookings(ItemDto dto, List<Booking> bookings, LocalDateTime now) {
        bookings.stream()
                .filter(booking -> !booking.getStart().isAfter(now))
                .reduce((first, second) -> second)
                .map(BookingMapper::toBookingShortDto)
                .ifPresent(dto::setLastBooking);
        bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .findFirst()
                .map(BookingMapper::toBookingShortDto)
                .ifPresent(dto::setNextBooking);
    }
}
