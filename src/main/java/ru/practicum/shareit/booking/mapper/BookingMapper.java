package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()));
    }

    public Booking toBooking(BookingShortDto booking) {
        return new Booking(booking.getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public BookingForItemDto bookingForItemDto(Booking booking) {
        return new BookingForItemDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId());
    }
}
