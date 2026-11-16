package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;

@JsonTest
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeDates() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2026, 1, 1, 12, 30, 15));
        dto.setEnd(LocalDateTime.of(2026, 1, 2, 12, 30, 15));
        dto.setStatus(Status.WAITING);

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-01-01T12:30:15");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-01-02T12:30:15");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}