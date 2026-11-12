package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponse create(ItemRequest itemRequest, Long userId) {
        userService.existsById(userId);

        return ItemMapper.mapToDto(itemRepository.save(ItemMapper.mapToEntity(itemRequest), userId));
    }

    @Override
    public ItemResponse update(ItemRequest itemRequest, Long itemId, Long userId) {
        itemRepository.validExists(itemId);

        UserResponse user = userService.getById(userId);
        Item saved = itemRepository.getById(itemId);

        validOwner(user.getId(), saved.getOwnerId());

        Item updated = ItemMapper.mapToUpdate(saved, itemRequest);
        itemRepository.save(updated, userId);

        log.info("Обновили вещь с id {}", updated.getId());

        return ItemMapper.mapToDto(updated);
    }

    @Override
    public ItemResponse getById(Long itemId, Long userId) {
        userService.existsById(userId);

        return ItemMapper.mapToDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemResponse> getAll(Long userId) {
        UserResponse user = userService.getById(userId);

        return itemRepository.getAll(userId).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemResponse> search(String text, Long userId) {
        userService.existsById(userId);

        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToDto)
                .toList();
    }

    private void validOwner(Long userId, Long ownerId) {
        if (!userId.equals(ownerId)) {
            log.warn("Попытка обновить вещь пользователя {} пользователем {}", ownerId, userId);
            throw new ValidationException("Обновить данные о вещи может только её владелец!");
        }
    }
}
