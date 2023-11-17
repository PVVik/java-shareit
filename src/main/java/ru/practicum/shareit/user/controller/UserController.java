package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("createUser" + user);
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("getAllUsers");
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        log.info("updateUser" + user);
        return userService.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        log.info("getById" + id);
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("deleteById" + id);
        userService.deleteById(id);
    }
}
