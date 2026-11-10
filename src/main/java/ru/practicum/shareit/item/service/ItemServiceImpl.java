package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1L);
    private final UserService userService;

    @Override
    public ItemResponse create(ItemRequest itemRequest, Long userId) {
        UserResponse user = userService.getById(userId);
        Item item = ItemMapper.mapToEntity(itemRequest);

        item.setOwnerId(user.getId());
        item.setId(ids.getAndIncrement());
        items.put(item.getId(), item);

        log.info("Добавили вещь с id {}", item.getId());

        return ItemMapper.mapToDto(item);
    }

    @Override
    public ItemResponse update(ItemRequest itemRequest, Long itemId, Long userId) {
        validExists(itemId);

        UserResponse user = userService.getById(userId);
        Item saved = items.get(itemId);
        items.remove(saved.getId());

        validOwner(user.getId(), saved.getOwnerId());

        Item updated = ItemMapper.mapToUpdate(saved, itemRequest);
        items.put(updated.getId(), updated);

        log.info("Обновили вещь с id {}", updated.getId());

        return ItemMapper.mapToDto(updated);
    }

    @Override
    public ItemResponse getById(Long itemId) {
        validExists(itemId);

        Item item = items.get(itemId);

        log.info("Вернули вещь по id {}", itemId);

        return ItemMapper.mapToDto(item);
    }

    @Override
    public List<ItemResponse> getAll(Long userId) {
        UserResponse user = userService.getById(userId);

        List<Item> userItems = items.values().stream()
                .filter(i -> i.getOwnerId().equals(user.getId()))
                .toList();

        log.info("Получили список вещей пользователя {}", user.getId());

        return userItems.stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemResponse> search(String text, Long userId) {
        if (!userService.existsById(userId)) {
            log.warn("Попытка запросить пользователя по несуществующему id {}", userId);
            throw new NotFoundException(String.format("Пользователя с переданным id %d не существует!", userId));
        }
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> finded = items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::isAvailable)
                .toList();

        log.info("Получили выгрузку вещей по запросу {}", text);

        return finded.stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    private void validExists(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn("Не удалось найти вещь по несуществующему id {}", itemId);
            throw new NotFoundException(String.format("Вещи с id %d не существует!", itemId));
        }
    }

    private void validOwner(Long userId, Long ownerId) {
        if (!userId.equals(ownerId)) {
            log.warn("Попытка обновить вещь пользователя {} пользователем {}", ownerId, userId);
            throw new ValidationException("Обновить данные о вещи может только её владелец!");
        }
    }
}
