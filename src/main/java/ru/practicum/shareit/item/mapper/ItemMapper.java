package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentService commentService;
    private final BookingService bookingService;

    public Item toItem(ItemShortDto item) {
        return new Item(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                null,
                commentService.getCommentsByItemId(item.getId()),
                item.getRequestId());
    }

    public ItemDto toItemExtendedDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                bookingService.getLastBooking(item.getId()),
                bookingService.getNextBooking(item.getId()),
                commentService.getCommentsByItemId(item.getId()),
                item.getRequestId());
    }
}