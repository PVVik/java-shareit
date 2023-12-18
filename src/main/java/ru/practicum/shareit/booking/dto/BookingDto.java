package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Booker booker;
    private Item item;

    @Data
    @AllArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}