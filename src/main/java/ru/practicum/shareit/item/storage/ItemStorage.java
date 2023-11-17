package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto createItem(ItemDto item);

    void saveUsersItem(Long userId, Long itemId);

    ItemDto updateItem(Long itemId, ItemDto item);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByUserId(Long id);

    List<ItemDto> search(String text);

    boolean isUsersItem(Long userId, Long itemId);
}
