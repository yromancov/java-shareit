package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ParameterNotValidException;

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

    @Test
    void createBookingWithInvalidData() {
        ItemDto unavailable = TestData.item("Saw");
        unavailable.setAvailable(false);
        ItemDto saved = itemService.createItem(owner.getId(), unavailable);

        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(),
                TestData.booking(999L, now.plusDays(1), now.plusDays(2))))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(),
                TestData.booking(saved.getId(), now.plusDays(1), now.plusDays(2))))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> bookingService.createBooking(owner.getId(),
                TestData.booking(item.getId(), now.plusDays(1), now.plusDays(2))))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> book(now.plusDays(2), now.plusDays(1)))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void rejectBooking() {
        BookingDto booking = book(now.plusDays(1), now.plusDays(2));

        assertThat(bookingService.isApproved(owner.getId(), booking.getId(), false).getStatus())
                .isEqualTo(Status.REJECTED);
    }

    @Test
    void isApprovedByNotOwnerOrTwice() {
        BookingDto booking = book(now.plusDays(1), now.plusDays(2));

        assertThatThrownBy(() -> bookingService.isApproved(booker.getId(), booking.getId(), true))
                .isInstanceOf(ParameterNotValidException.class);
        bookingService.isApproved(owner.getId(), booking.getId(), true);
        assertThatThrownBy(() -> bookingService.isApproved(owner.getId(), booking.getId(), true))
                .isInstanceOf(ParameterNotValidException.class);
        assertThatThrownBy(() -> bookingService.isApproved(owner.getId(), 999L, true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingById() {
        BookingDto booking = book(now.plusDays(1), now.plusDays(2));
        UserDto stranger = userService.createUser(TestData.user("stranger"));

        assertThat(bookingService.getBookingById(booker.getId(), booking.getId()).getId()).isEqualTo(booking.getId());
        assertThat(bookingService.getBookingById(owner.getId(), booking.getId()).getId()).isEqualTo(booking.getId());
        assertThatThrownBy(() -> bookingService.getBookingById(stranger.getId(), booking.getId()))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> bookingService.getBookingById(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingsByAllStates() {
        BookingDto past = book(now.minusDays(3), now.minusDays(2));
        BookingDto current = book(now.minusDays(1), now.plusDays(1));
        BookingDto future = book(now.plusDays(1), now.plusDays(2));
        bookingService.isApproved(owner.getId(), past.getId(), true);
        bookingService.isApproved(owner.getId(), current.getId(), false);

        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.CURRENT))
                .extracting(BookingDto::getId).containsExactly(current.getId());
        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.PAST))
                .extracting(BookingDto::getId).containsExactly(past.getId());
        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.WAITING))
                .extracting(BookingDto::getId).containsExactly(future.getId());
        assertThat(bookingService.getBookingsByBooker(booker.getId(), BookingState.REJECTED))
                .extracting(BookingDto::getId).containsExactly(current.getId());
        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.CURRENT))
                .extracting(BookingDto::getId).containsExactly(current.getId());
        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.FUTURE))
                .extracting(BookingDto::getId).containsExactly(future.getId());
        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.WAITING))
                .extracting(BookingDto::getId).containsExactly(future.getId());
        assertThat(bookingService.getBookingsByOwner(owner.getId(), BookingState.REJECTED))
                .extracting(BookingDto::getId).containsExactly(current.getId());
    }

    @Test
    void getBookingsForUnknownUser() {
        assertThatThrownBy(() -> bookingService.getBookingsByBooker(999L, BookingState.ALL))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> bookingService.getBookingsByOwner(999L, BookingState.ALL))
                .isInstanceOf(NotFoundException.class);
    }
}
