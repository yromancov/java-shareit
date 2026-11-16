package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@JsonTest
class BookItemRequestDtoJsonTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    private String body(LocalDateTime start, LocalDateTime end) {
        return "{\"itemId\":1,\"start\":\"" + start + "\",\"end\":\"" + end + "\"}";
    }

    @Test
    void validBooking() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 12, 30, 15);
        LocalDateTime end = start.plusDays(1);

        BookItemRequestDto dto = json.parseObject(body(start, end));

        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void endEqualsStart_invalid() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 12, 30, 15);

        BookItemRequestDto dto = json.parseObject(body(start, start));

        assertThat(validator.validate(dto)).isNotEmpty();
    }

    @Test
    void startInPast_invalid() throws Exception {
        BookItemRequestDto dto = json.parseObject(body(LocalDateTime.of(2020, 1, 1, 12, 0),
                LocalDateTime.of(2030, 1, 1, 12, 0)));

        assertThat(validator.validate(dto)).isNotEmpty();
    }
}