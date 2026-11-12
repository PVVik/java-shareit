package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class ItemRepository {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1L);

    public Item save(Item item, Long userId) {
        item.setOwnerId(userId);
        if (item.getId() == null) {
            item.setId(ids.getAndIncrement());
        }

        items.put(item.getId(), item);

        log.info("Добавили вещь с id {}", item.getId());

        return item;
    }

    public Item getById(Long itemId) {
        validExists(itemId);
        Item item = items.get(itemId);

        log.info("Вернули вещь по id {}", itemId);

        return item;
    }

    public List<Item> getAll(Long userId) {
        List<Item> userItems = items.values().stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .toList();

        log.info("Получили список вещей пользователя {}", userId);

        return userItems;
    }

    public List<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> finded = items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .toList();

        log.info("Получили выгрузку вещей по запросу {}", text);

        return finded;
    }

    public void validExists(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn("Не удалось найти вещь по несуществующему id {}", itemId);
            throw new NotFoundException(String.format("Вещи с id %d не существует!", itemId));
        }
    }

}
