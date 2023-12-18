package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingForItemDtoTest {
    @Autowired
    private JacksonTester<BookingForItemDto> json;
    private final BookingForItemDto booking = new BookingForItemDto(
            1L,
            LocalDateTime.of(2020, 10, 10, 10, 10, 0),
            LocalDateTime.of(2020, 12, 10, 10, 10, 0),
            1L,
            1L);

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingForItemDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-10-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2020-12-10T10:10:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
