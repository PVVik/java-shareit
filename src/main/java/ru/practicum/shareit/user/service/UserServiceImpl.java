package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse create(UserRequest userRequest) {
        return UserMapper.mapToDto(userRepository.save(UserMapper.mapToEntity(userRequest)));
    }

    @Override
    public UserResponse update(UserRequest userRequest, Long userId) {
        userRepository.validExists(userId);

        User saved = userRepository.getById(userId);
        User updated = UserMapper.mapToUpdateUser(saved, userRequest);

        userRepository.save(updated);

        log.info("Обновили пользователя с id {}", updated.getId());

        return UserMapper.mapToDto(updated);
    }

    @Override
    public UserResponse getById(Long userId) {
        return UserMapper.mapToDto(userRepository.getById(userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public void existsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Попытка запросить пользователя по несуществующему id {}", userId);
            throw new NotFoundException(String.format("Пользователя с переданным id %d не существует!", userId));
        }
    }
}
