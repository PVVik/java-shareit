package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.enums.State;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingShortDto booking);

    BookingDto updateStatus(Long userId, Long booking, Boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByOwner(Long userId, State state);

    List<BookingDto> getBookingsByUser(Long userId, State state);
}
