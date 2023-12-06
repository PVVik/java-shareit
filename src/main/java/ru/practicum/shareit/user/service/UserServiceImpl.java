package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User createUser(User user) {
        log.info("createUser " + user);
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("updateUser " + id);
        return userStorage.updateUser(id, user);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("getAllUsers");
        return userStorage.getAllUsers();
    }

    @Override
    public User getById(Long id) {
        log.info("getById " + id);
        return userStorage.getById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("deleteById " + id);
        userStorage.deleteById(id);
    }
}
