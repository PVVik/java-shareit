package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()));
    }

    public Booking toBooking(BookingShortDto booking) {
        return new Booking(
                booking.getStart(),
                booking.getEnd());
    }

    public BookingForItemDto bookingForItemDto(Booking booking) {
        if (booking != null) {
            return new BookingForItemDto(
                    booking.getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getItem().getId(),
                    booking.getBooker().getId());
        } else {
            return null;
        }
    }
}