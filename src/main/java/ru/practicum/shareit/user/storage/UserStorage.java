package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(Long id, User user);

    List<User> getAllUsers();

    User getById(Long userId);

    void deleteById(Long id);
}
