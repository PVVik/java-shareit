package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @PostMapping
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody RequestDto request) {
        return service.create(userId, request, LocalDateTime.now());
    }

    @GetMapping
    public List<RequestDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        return service.getExistingRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("requestId") Long requestId) {
        return service.getRequestById(userId, requestId);
    }
}