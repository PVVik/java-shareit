package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        ItemDto thisItem = new ItemDto();
        if (userStorage.getById(userId) != null) {
            thisItem = itemStorage.createItem(item);
            itemStorage.saveUsersItem(userId, thisItem.getId());
        } else {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        return thisItem;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        ItemDto thisItem = new ItemDto();
        if (userStorage.getById(userId) != null && itemStorage.getItemById(itemId) != null) {
            if (itemStorage.isUsersItem(userId, itemId)) {
                thisItem = itemStorage.updateItem(itemId, item);
            } else {
                throw new ObjectNotFoundException("Пользователь не является владельцем товара");
            }
        }
        return thisItem;
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long id) {
        return itemStorage.getItemsByUserId(id);
    }


    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text);
    }
}
