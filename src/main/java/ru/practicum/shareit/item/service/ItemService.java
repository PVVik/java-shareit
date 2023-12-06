package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> getItemsByUserId(Long id);

    List<ItemDto> search(String text);
}
