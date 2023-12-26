package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemShortDto {
    private Long id;
    @NotBlank(message = "Поле названия товара не может состоять из пробелов", groups = {Create.class})
    private String name;
    @NotNull(message = "Поле описания товара не должно быть пустым", groups = {Create.class})
    private String description;
    @NotNull(message = " Поле статуса доступности не может быть пустым", groups = {Create.class})
    private Boolean available;
    private Long requestId;
}
