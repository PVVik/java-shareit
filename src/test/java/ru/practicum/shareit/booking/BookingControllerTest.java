package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.item.dto.ItemDto;

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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private final ItemDto item = new ItemDto(
            1L,
            "Sunglasses",
            "The coolest ever",
            true,
            null);
    private final BookingShortDto inputBooking = new BookingShortDto(
            LocalDateTime.of(2024, 10, 10, 10, 10, 0),
            LocalDateTime.of(2024, 12, 10, 10, 10, 0),
            item.getId());
    private final BookingDto outputBooking = new BookingDto(
            1L,
            LocalDateTime.of(2024, 10, 10, 10, 10, 0),
            LocalDateTime.of(2024, 12, 10, 10, 10, 0),
            null,
            new BookingDto.Booker(10L, "Ivan"),
            new BookingDto.Item(item.getId(), item.getName()));

    @SneakyThrows
    @Test
    void create_shouldCreateBooking() {
        when(bookingService.create(any(Long.class), any()))
                .thenReturn(outputBooking);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(inputBooking))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(outputBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outputBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outputBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getById_shouldReturnBookingById() {
        when(bookingService.getById(any(Long.class), any()))
                .thenReturn(outputBooking);

        mvc.perform(get("/bookings/1")
                        .content(objectMapper.writeValueAsString(inputBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(outputBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outputBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outputBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getBookingsByUser_shouldReturnListOfBookings() {
        when(bookingService.getBookingsByUser(any(Long.class), any(State.class),
                any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(outputBooking));

        mvc.perform(get("/bookings")
                        .content(objectMapper.writeValueAsString(inputBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(outputBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start",
                        is(outputBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end",
                        is(outputBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void getBookingsByOwner_shouldReturnListOfBookings() {
        when(bookingService.getBookingsByOwner(any(Long.class), any(State.class),
                any(Integer.class), nullable(Integer.class)))
                .thenReturn(List.of(outputBooking));

        mvc.perform(get("/bookings/owner?from=0&size=10")
                        .content(objectMapper.writeValueAsString(inputBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(outputBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(outputBooking.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(outputBooking.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @SneakyThrows
    @Test
    void update_shouldUpdateBooking() {
        when(bookingService.updateStatus(any(Long.class), any(Long.class), any(Boolean.class)))
                .thenReturn(outputBooking);

        mvc.perform(patch("/bookings/1")
                        .content(objectMapper.writeValueAsString(inputBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(outputBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(outputBooking.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(outputBooking.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

}