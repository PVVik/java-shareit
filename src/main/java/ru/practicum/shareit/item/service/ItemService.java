package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse create(ItemRequest itemRequest, Long userId);

    ItemResponse update(ItemRequest itemRequest, Long itemId, Long userId);

    ItemResponse getById(Long itemId, Long userId);

    List<ItemResponse> getAll(Long userId);

    List<ItemResponse> search(String text, Long userId);
}
