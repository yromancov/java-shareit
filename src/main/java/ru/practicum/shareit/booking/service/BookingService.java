package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long bookerId, BookingDto dto);

    BookingDto getBookingById(long bookingId);

    List<BookingDto> getBookings();

    BookingDto editBooking(long bookerId, long bookingId, BookingDto dto);

    void removeBooking(long bookingId);

    Status isApproved(long bookingId, boolean approved);

    List<BookingDto> getBookingsByBookerId(long bookerId, Status status);

    List<BookingDto> getBookingsItemsBooker(long bookerId, Status status);
}
