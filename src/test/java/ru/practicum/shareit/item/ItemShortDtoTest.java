package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemShortDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemShortDtoTest {
    @Autowired
    private JacksonTester<ItemShortDto> json;
    private final ItemShortDto itemDto = new ItemShortDto(
            2L,
            "Kids toy",
            "An old and dirty one",
            true,
            null);

    @SneakyThrows
    @Test
    void testItemShortDto() {
        JsonContent<ItemShortDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Kids toy");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("An old and dirty one");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.request_id").isEqualTo(null);
    }

}
