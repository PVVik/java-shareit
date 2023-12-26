package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {
    @NotNull(message = "Время создания заявки на бронирование не должно быть пустым")
    @FutureOrPresent(message = "Время начала заявки не должно быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Время окончания заявки на бронирование не должно быть пустым")
    @Future(message = "Время окончания заявки не может быть в прошлом")
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}
