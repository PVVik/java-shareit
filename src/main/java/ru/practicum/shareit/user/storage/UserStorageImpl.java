package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Невалидный почтовый ящик");
        }
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            User thisUser = entry.getValue();
            if (user.getEmail().equals(thisUser.getEmail())) {
                throw new ObjectAlreadyExistsException("Почтовый ящик уже есть в системе");
            }
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        User thisUser = users.get(id);
        if (user.getEmail() != null) {
            if (!user.getEmail().equals(thisUser.getEmail())) {
                for (Map.Entry<Long, User> entry : users.entrySet()) {
                    User currentUser = entry.getValue();
                    if (user.getEmail().equals(currentUser.getEmail())) {
                        throw new ObjectAlreadyExistsException("Почтовый ящик уже есть в системе");
                    }
                }
                thisUser.setEmail(user.getEmail());
            }
        }
        if (user.getName() != null) {
            thisUser.setName(user.getName());
        }
        users.put(id, thisUser);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new ValidationException("Пользователя с подобным идентификатором не существует");
        }
        users.remove(id);
    }
}
