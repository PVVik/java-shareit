package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private final BookingDto.Item item = new BookingDto.Item(
            1L,
            "Scissors");
    private final BookingDto.Booker user = new BookingDto.Booker(
            1L,
            "Maria");
    private final BookingDto booking = new BookingDto(
            1L,
            LocalDateTime.of(2020, 10, 10, 10, 10, 0),
            LocalDateTime.of(2020, 12, 10, 10, 10, 0),
            Status.WAITING,
            user,
            item);

    @SneakyThrows
    @Test
    void testBookingDto() {
        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2020-10-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2020-12-10T10:10:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Maria");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Scissors");
    }
}
