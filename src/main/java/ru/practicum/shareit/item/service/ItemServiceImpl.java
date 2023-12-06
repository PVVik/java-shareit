package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        ItemDto thisItem;
        if (existsById(userId)) {
            thisItem = itemStorage.createItem(item);
            itemStorage.saveUsersItem(userId, thisItem.getId());
        } else {
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        log.info("createItem " + item);
        return thisItem;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        ItemDto thisItem = new ItemDto();
        if (existsById(userId) && itemStorage.getItemById(itemId) != null) {
            if (itemStorage.isUsersItem(userId, itemId)) {
                thisItem = itemStorage.updateItem(itemId, item);
            } else {
                throw new ObjectNotFoundException("Пользователь не является владельцем товара");
            }
        }
        log.info("updateItem " + itemId);
        return thisItem;
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        log.info("getItemById " + id);
        return itemStorage.getItemById(id);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long id) {
        log.info("getItemsByUserId " + id);
        return itemStorage.getItemsByUserId(id);
    }


    @Override
    public List<ItemDto> search(String text) {
        log.info("search");
        return itemStorage.search(text);
    }

    private boolean existsById(Long userId) {
        return userStorage.getById(userId) != null;
    }
}
