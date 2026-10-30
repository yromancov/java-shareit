package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody NewBookingDto dto) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                     @PathVariable("bookingId") long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.isApproved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@PathVariable("bookingId") long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllBookingsCurrentBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                        @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByBooker(bookerId, state);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getListBookingForItemOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                       @RequestParam(defaultValue = "ALL") BookingState state){
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}
