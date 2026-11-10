package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest userRequest) {
        return userService.create(userRequest);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@RequestBody UserRequest userRequest, @PathVariable Long userId) {
        return userService.update(userRequest, userId);
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
