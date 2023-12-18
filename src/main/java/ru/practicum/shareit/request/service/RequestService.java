package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    RequestDto create(Long userId, RequestDto request, LocalDateTime dateTime);

    List<RequestDto> getRequestsByOwner(Long userId);

    List<RequestDto> getExistingRequests(Long userId, Integer from, Integer size);

    RequestDto getRequestById(Long userId, Long requestId);
}
