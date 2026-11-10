package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserResponse {

    private long id;

    private String email;

    private String name;
}
