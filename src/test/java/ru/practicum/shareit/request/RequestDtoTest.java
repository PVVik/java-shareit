package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<RequestDto> json;
    private final UserDto user = new UserDto(
            2L,
            "Andrew",
            "andrew@mail.ru");
    private final RequestDto request = new RequestDto(
            1L,
            "I need a screwdriver",
            user,
            LocalDateTime.of(2023, 10, 10, 14, 30, 0),
            null);

    @SneakyThrows
    @Test
    void testRequestDto() {
        JsonContent<RequestDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("I need a screwdriver");
        assertThat(result).extractingJsonPathNumberValue("$.requester.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo("Andrew");
        assertThat(result).extractingJsonPathStringValue("$.requester.email")
                .isEqualTo("andrew@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-10-10T14:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(null);
    }
}
