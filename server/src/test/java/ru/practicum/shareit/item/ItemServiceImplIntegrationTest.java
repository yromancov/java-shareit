package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        owner = userService.createUser(TestData.user("owner"));
        booker = userService.createUser(TestData.user("booker"));
        item = itemService.createItem(owner.getId(), TestData.item("Drill"));
    }

    private void approvedBooking(LocalDateTime start, LocalDateTime end) {
        BookingDto booking = bookingService.createBooking(booker.getId(),
                TestData.booking(item.getId(), start, end));
        bookingService.isApproved(owner.getId(), booking.getId(), true);
    }

    private NewCommentDto comment(String text) {
        NewCommentDto dto = new NewCommentDto();
        dto.setText(text);
        return dto;
    }

    @Test
    void createItem() {
        NewItemRequestDto requestDto = new NewItemRequestDto();
        requestDto.setDescription("Нужна дрель");
        ItemRequestDto request = itemRequestService.createRequests(booker.getId(), requestDto);
        ItemDto dto = TestData.item("Drill 2");
        dto.setRequestId(request.getId());

        ItemDto saved = itemService.createItem(owner.getId(), dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void getItemsByOwner() {
        approvedBooking(now.minusDays(3), now.minusDays(2));
        approvedBooking(now.plusDays(1), now.plusDays(2));
        itemService.addComment(booker.getId(), item.getId(), comment("Отличная дрель"));

        List<ItemDto> items = itemService.getItemsByOwner(owner.getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0).getLastBooking()).isNotNull();
        assertThat(items.get(0).getNextBooking()).isNotNull();
        assertThat(items.get(0).getComments()).hasSize(1);
    }

    @Test
    void getItemById() {
        approvedBooking(now.minusDays(3), now.minusDays(2));

        ItemDto result = itemService.getItemById(owner.getId(), item.getId());

        assertThat(result.getName()).isEqualTo("Drill");
        assertThat(result.getLastBooking()).isNotNull();
    }

    @Test
    void addComment() {
        approvedBooking(now.minusDays(3), now.minusDays(2));

        CommentDto saved = itemService.addComment(booker.getId(), item.getId(), comment("Отличная дрель"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAuthorName()).isEqualTo("booker");
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    void createItemWithInvalidFields() {
        ItemDto noName = TestData.item("x");
        noName.setName(null);
        ItemDto noDescription = TestData.item("x");
        noDescription.setDescription(" ");
        ItemDto noAvailable = TestData.item("x");
        noAvailable.setAvailable(null);

        assertThatThrownBy(() -> itemService.createItem(owner.getId(), noName))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> itemService.createItem(owner.getId(), noDescription))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> itemService.createItem(owner.getId(), noAvailable))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> itemService.createItem(999L, TestData.item("x")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createItemWithUnknownRequest() {
        ItemDto dto = TestData.item("Drill 2");
        dto.setRequestId(999L);

        assertThatThrownBy(() -> itemService.createItem(owner.getId(), dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void editItem() {
        ItemDto patch = new ItemDto();
        patch.setName("Drill PRO");
        patch.setDescription("new description");
        patch.setAvailable(false);

        ItemDto updated = itemService.editItem(owner.getId(), item.getId(), patch);

        assertThat(updated.getName()).isEqualTo("Drill PRO");
        assertThat(updated.getDescription()).isEqualTo("new description");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void editItemByNotOwner() {
        assertThatThrownBy(() -> itemService.editItem(booker.getId(), item.getId(), new ItemDto()))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> itemService.editItem(owner.getId(), 999L, new ItemDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void searchItems() {
        assertThat(itemService.searchItems("dRiLl")).hasSize(1);
        assertThat(itemService.searchItems(" ")).isEmpty();
    }

    @Test
    void getItemsByOwnerWithoutItems() {
        assertThat(itemService.getItemsByOwner(booker.getId())).isEmpty();
    }

    @Test
    void getItemByIdForNotOwner() {
        approvedBooking(now.minusDays(3), now.minusDays(2));

        assertThat(itemService.getItemById(booker.getId(), item.getId()).getLastBooking()).isNull();
        assertThatThrownBy(() -> itemService.getItemById(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addCommentWithoutBooking() {
        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), comment("text")))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> itemService.addComment(booker.getId(), 999L, comment("text")))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> itemService.addComment(999L, item.getId(), comment("text")))
                .isInstanceOf(NotFoundException.class);
    }
}
