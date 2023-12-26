package ru.practicum.shareit.comment;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private final CommentDto comment = new CommentDto(
            1L,
            "User your toothbrush, suppose need a new one",
            "Andrew",
            LocalDateTime.of(2020, 10, 15, 3, 30, 0));

    @SneakyThrows
    @Test
    void testCommentDto() {
        JsonContent<CommentDto> result = json.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("User your toothbrush, suppose need a new one");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Andrew");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2020-10-15T03:30:00");
    }
}