package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(1L);

    @Override
    public UserResponse create(UserRequest userRequest) {
        validEmail(userRequest.getEmail());
        User user = UserMapper.mapToEntity(userRequest);

        user.setId(ids.getAndIncrement());
        users.put(user.getId(), user);

        log.info("Добавили юзера с id {}", user.getId());

        return UserMapper.mapToDto(user);
    }

    @Override
    public UserResponse update(UserRequest userRequest, Long userId) {
        validExists(userId);
        if (userRequest.getEmail() != null) {
            validEmail(userRequest.getEmail());
        }

        User saved = users.get(userId);
        users.remove(saved.getId());

        User updated = UserMapper.mapToUpdateUser(saved, userRequest);
        users.put(updated.getId(), updated);

        log.info("Обновили пользователя с id {}", updated.getId());

        return UserMapper.mapToDto(updated);
    }

    @Override
    public UserResponse getById(Long userId) {
        validExists(userId);

        User user = users.get(userId);

        log.info("Вернули пользователя по id {}", userId);

        return UserMapper.mapToDto(user);
    }

    @Override
    public void delete(Long userId) {
        validExists(userId);
        users.remove(userId);

        log.info("Удалили пользователя по id {}", userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    private void validEmail(String email) {
        List<User> list = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .toList();

        if (!list.isEmpty()) {
            log.warn("Попытка создать пользователя с уже существующим email {}", email);
            throw new AlreadyExistsException(String.format("Пользователь с email %s уже существует!", email));
        }
    }

    private void validExists(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Попытка запросить пользователя по несуществующему id {}", id);
            throw new NotFoundException(String.format("Пользователя с переданным id %d не существует!", id));
        }
    }

}
