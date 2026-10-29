package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(long bookerId, BookingDto dto) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long bookingId) {
        return null;
    }

    @Override
    public List<BookingDto> getBookings() {
        return List.of();
    }

    @Override
    public BookingDto editBooking(long bookerId, long bookingId, BookingDto dto) {
        return null;
    }

    @Override
    public void removeBooking(long bookingId) {

    }

    @Override
    public Status isApproved(long bookingId, boolean approved) {
        return null;
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(long bookerId, Status status) {
        return List.of();
    }

    @Override
    public List<BookingDto> getBookingsItemsBooker(long bookerId, Status status) {
        return List.of();
    }
}
