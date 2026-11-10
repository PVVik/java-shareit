package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User mapToEntity(UserRequest userRequest) {
        User user = new User();

        user.setEmail(userRequest.getEmail());
        if (userRequest.getName() != null) {
            user.setName(userRequest.getName());
        }

        return user;
    }

    public static UserResponse mapToDto(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        if (user.getName() != null) {
            userResponse.setName(user.getName());
        }

        return userResponse;
    }

    public static User mapToUpdateUser(User user, UserRequest userDto) {
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return user;
    }
}
