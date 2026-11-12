package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class UserRepository {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1L);

    public User save(User user) {
        validEmail(user.getEmail(), user.getId() != null ? user.getId() : null);

        if (user.getId() == null) {
            user.setId(ids.getAndIncrement());
        }

        users.put(user.getId(), user);

        log.info("Добавили юзера с id {}", user.getId());

        return user;
    }

    public User getById(Long userId) {
        validExists(userId);
        User user = users.get(userId);

        log.info("Вернули пользователя по id {}", userId);

        return user;
    }

    public void delete(Long userId) {
        validExists(userId);
        users.remove(userId);

        log.info("Удалили пользователя по id {}", userId);
    }

    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    public void validEmail(String email, Long id) {
        List<User> list = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> {
                    if (id != null) {
                        return !u.getId().equals(id);
                    }
                    return true;
                })
                .toList();

        if (!list.isEmpty()) {
            log.warn("Попытка создать пользователя с уже существующим email {}", email);
            throw new AlreadyExistsException(String.format("Пользователь с email %s уже существует!", email));
        }
    }

    public void validExists(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Попытка запросить пользователя по несуществующему id {}", id);
            throw new NotFoundException(String.format("Пользователя с переданным id %d не существует!", id));
        }
    }
}
