package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {

    UserResponse create(UserRequest userRequest);

    UserResponse update(UserRequest userDto, Long userId);

    UserResponse getById(Long userId);

    void delete(Long userId);

    void existsById(Long userId);
}
