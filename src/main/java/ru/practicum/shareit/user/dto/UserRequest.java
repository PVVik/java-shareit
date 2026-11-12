package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Почта пользователя обязательна!")
    @Email(message = "Проверьте формат почты!")
    private String email;

    private String name;
}
