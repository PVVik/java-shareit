package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final UserMapper userMapper;
    private final UserService userService;
    private final ItemService itemService;

    public Request toRequest(RequestDto request, Long userId, LocalDateTime localDateTime) {
        return new Request(
                null,
                request.getDescription(),
                userMapper.toUser(userService.getById(userId)),
                localDateTime);
    }

    public RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                userMapper.toUserDto(request.getRequester()),
                request.getCreated(),
                itemService.getItemsByRequestId(request.getId()));
    }
}
