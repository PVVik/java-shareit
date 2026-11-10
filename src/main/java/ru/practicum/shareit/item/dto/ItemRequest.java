package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {

    @NotBlank(message = "Название вещи обязательно!")
    private String name;

    @NotBlank(message = "Описание вещи обязательно!")
    private String description;

    @NotNull(message = "Статус вещи обязателен!")
    private Boolean available;

}
