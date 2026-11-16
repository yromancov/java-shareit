package ru.practicum.shareit.booking;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.practicum.shareit.TestData;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;

    private BookingDto booking(Status status) {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItem(TestData.item("Drill"));
        dto.setBooker(TestData.user("booker"));
        dto.setStatus(status);
        return dto;
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(eq(2L), any())).thenReturn(booking(Status.WAITING));

        mvc.perform(post("/bookings").header(USER_HEADER, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(TestData.booking(1L,
                                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.isApproved(1L, 1L, true)).thenReturn(booking(Status.APPROVED));

        mvc.perform(patch("/bookings/1").header(USER_HEADER, 1).param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(2L, 1L)).thenReturn(booking(Status.WAITING));

        mvc.perform(get("/bookings/1").header(USER_HEADER, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBookingsByBooker() throws Exception {
        when(bookingService.getBookingsByBooker(2L, BookingState.ALL)).thenReturn(List.of(booking(Status.WAITING)));

        mvc.perform(get("/bookings").header(USER_HEADER, 2).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByOwner() throws Exception {
        when(bookingService.getBookingsByOwner(1L, BookingState.ALL)).thenReturn(List.of(booking(Status.APPROVED)));

        mvc.perform(get("/bookings/owner").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}