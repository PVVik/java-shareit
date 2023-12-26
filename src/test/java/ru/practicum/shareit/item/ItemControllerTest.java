package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private final User andrew = new User(
            1L,
            "Andrew",
            "andrew@mail.ru");
    private final ItemDto toy = new ItemDto(
            2L,
            "A toy",
            "An old and grumpy toy",
            true,
            null);
    private final CommentDto comment = new CommentDto(
            4L,
            "I used it and I am happy now",
            andrew.getName(),
            LocalDateTime.now().minusDays(30));

    @SneakyThrows
    @Test
    void create_shouldCreateItem() {
        when(itemService.create(any(Long.class), any()))
                .thenReturn(toy);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(toy))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(toy.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(toy.getName())))
                .andExpect(jsonPath("$.description", is(toy.getDescription())))
                .andExpect(jsonPath("$.available", is(toy.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnItemById() {
        when(itemService.getById(any(Long.class), any(Long.class)))
                .thenReturn(toy);

        mvc.perform(get("/items/1")
                        .content(objectMapper.writeValueAsString(toy))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(toy.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(toy.getName())))
                .andExpect(jsonPath("$.description", is(toy.getDescription())))
                .andExpect(jsonPath("$.available", is(toy.getAvailable())));
    }

    @SneakyThrows
    @Test
    void getByUserId_shouldReturnListOfItems() {
        when(itemService.getByUserId(any(Long.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(toy));

        mvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(toy))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(toy.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(toy.getName())))
                .andExpect(jsonPath("$.[0].description", is(toy.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(toy.getAvailable())));
    }

    @Test
    void deleteById_shouldDeleteById() throws Exception {
        mvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void update_shouldUpdateItem() {
        when(itemService.update(any(Long.class), any(Long.class), any()))
                .thenReturn(toy);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(toy))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(toy.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(toy.getName())))
                .andExpect(jsonPath("$.description", is(toy.getDescription())))
                .andExpect(jsonPath("$.available", is(toy.getAvailable())));
    }

    @SneakyThrows
    @Test
    void search_shouldReturnItemsList() {
        when(itemService.search(any(String.class), any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(toy));

        mvc.perform(get("/items/search?text=description")
                        .content(objectMapper.writeValueAsString(toy))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(toy.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(toy.getName())))
                .andExpect(jsonPath("$.[0].description", is(toy.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(toy.getAvailable())));
    }

    @SneakyThrows
    @Test
    void createComment_shouldCreateComment() {
        when(itemService.createComment(any(Long.class), any(Long.class), any()))
                .thenReturn(comment);

        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created",
                        is(comment.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}