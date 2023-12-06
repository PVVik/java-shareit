package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, ItemDto> items = new HashMap<>();
    private final Map<Long, Long> usersItems = new HashMap<>();
    private long id = 0;

    @Override
    public ItemDto createItem(ItemDto item) {
        id++;
        item.setId(id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void saveUsersItem(Long userId, Long itemId) {
        usersItems.put(userId, itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto item) {
        ItemDto thisItem = items.get(itemId);
        if (item.getName() != null) {
            thisItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            thisItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            thisItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, thisItem);
        return thisItem;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ObjectNotFoundException("Такого ид вещи не существует");
        }
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long id) {
        List<ItemDto> listItems = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : usersItems.entrySet()) {
            Long userId = entry.getKey();
            Long itemId = entry.getValue();
            if (id.equals(userId)) {
                listItems.add(getItemById(itemId));
            }
        }
        return listItems;
    }

    @Override
    public List<ItemDto> search(String text) {
        List<ItemDto> listItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Map.Entry<Long, ItemDto> entry : items.entrySet()) {
                ItemDto item = entry.getValue();
                if (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    if (item.getAvailable().equals(true)) {
                        listItems.add(item);
                    }
                }
            }
        }
        return listItems;
    }

    @Override
    public boolean isUsersItem(Long userId, Long itemId) {
        boolean isUsersItem = false;
        for (Map.Entry<Long, Long> entry : usersItems.entrySet()) {
            if (entry.getKey().equals(userId) && entry.getValue().equals(itemId)) {
                isUsersItem = true;
                break;
            }
        }
        return isUsersItem;
    }
}
