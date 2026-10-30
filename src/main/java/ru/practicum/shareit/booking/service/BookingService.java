package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long bookerId, NewBookingDto dto);

    BookingDto getBookingById(long bookingId);

    BookingDto isApproved(long ownerId,long bookingId, boolean approved);

    List<BookingDto> getBookingsByBooker(long bookerId, BookingState state);

    List<BookingDto> getBookingsByOwner(long ownerId, BookingState state);
}
