package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemResponse create(@Valid @RequestBody ItemRequest itemRequest,
                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.create(itemRequest, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestBody ItemRequest itemRequest,
                               @PathVariable Long itemId,
                               @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.update(itemRequest, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse getById(@PathVariable Long itemId,
                                @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> search(@RequestParam String text,
                                     @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return itemService.search(text, userId);
    }
}
