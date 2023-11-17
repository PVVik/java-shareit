package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto item) {
        log.info("createItem" + item);
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        log.info("updateItem" + item);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("getItemById" + itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("getItemsByUserId" + userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("search");
        return itemService.search(text);
    }
}
