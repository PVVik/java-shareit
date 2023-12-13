package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    UserDto getById(Long id);

    void deleteById(Long id);

    List<UserDto> getUsers();
}
