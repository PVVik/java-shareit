package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemShortDto item);

    ItemDto update(Long userId, Long itemId, ItemShortDto item);

    ItemDto getById(Long userId, Long itemId);

    CommentDto createComment(Long itemId, Long userId, CommentShortDto comment);

    void deleteById(Long userId, Long id);

    List<ItemDto> getByUserId(Long id);

    List<ItemShortDto> search(String text);
}
