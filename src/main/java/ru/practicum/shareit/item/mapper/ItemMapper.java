package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item mapToEntity(ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setAvailable(itemRequest.getAvailable());

        return item;
    }

    public static ItemResponse mapToDto(Item item) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(item.getId());
        itemResponse.setName(item.getName());
        itemResponse.setDescription(item.getDescription());
        itemResponse.setAvailable(item.isAvailable());

        return itemResponse;
    }

    public static Item mapToUpdate(Item item, ItemRequest itemRequest) {
        if (itemRequest.getName() != null) {
            item.setName(itemRequest.getName());
        }
        if (itemRequest.getDescription() != null) {
            item.setDescription(itemRequest.getDescription());
        }
        if (itemRequest.getAvailable() != null) {
            item.setAvailable(itemRequest.getAvailable());
        }

        return item;
    }
}
