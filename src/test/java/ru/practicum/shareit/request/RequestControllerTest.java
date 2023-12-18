package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;

    private final UserDto user = new UserDto(
            1L,
            "Anna",
            "anna@ya.ru");
    private final RequestDto request = new RequestDto(
            2L,
            "I need a comb",
            user,
            LocalDateTime.of(2022, 12, 10, 5, 5, 0),
            null);

    @SneakyThrows
    @Test
    void create_shouldCreateRequest() {
        when(requestService.create(any(Long.class), any(), any(LocalDateTime.class)))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(request.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(request.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(request.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequestById_shouldReturnListOfRequests() {
        when(requestService.getRequestById(any(Long.class), any(Long.class)))
                .thenReturn(request);

        mvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(request.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(request.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(request.getRequester().getEmail())))
                .andExpect(jsonPath("$.created",
                        is(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getExistingRequests_shouldReturnListOfRequests() {
        when(requestService.getExistingRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(request.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(request.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(request.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getRequestsByOwner_shouldReturnRequestsByOwner() {
        when(requestService.getRequestsByOwner(any(Long.class)))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(request.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.[0].requester.name", is(request.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(request.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created",
                        is(request.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }
}
