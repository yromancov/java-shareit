package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

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

    private BookingDto book(LocalDateTime start, LocalDateTime end) {
        return bookingService.createBooking(booker.getId(), TestData.booking(item.getId(), start, end));
    }

    @Test
    void createBooking() {
        BookingDto booking = book(now.plusDays(1), now.plusDays(2));

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void isApproved() {
        BookingDto booking = book(now.plusDays(1), now.plusDays(2));

        BookingDto approved = bookingService.isApproved(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getBookingsByBooker() {
        book(now.minusDays(3), now.minusDays(2));
        BookingDto future = book(now.plusDays(1), now.plusDays(2));

        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.ALL)).hasSize(2);
        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.FUTURE))
                .extracting(BookingDto::getId).containsExactly(future.getId());
    }

    @Test
    void getBookingsByOwner() {
        BookingDto past = book(now.minusDays(3), now.minusDays(2));
        book(now.plusDays(1), now.plusDays(2));

        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.ALL)).hasSize(2);
        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.PAST))
                .extracting(BookingDto::getId).containsExactly(past.getId());
    }
}