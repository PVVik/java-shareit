package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;
    @NotNull(message = "Имя не должно быть пустым.")
    @NotBlank(message = "Имя не должно состоять из пробелов.")
    private String name;
    @NotNull(message = "Описание не должно быть пустым.")
    private String description;
    @NotNull(message = "Статус не может быть пустым.")
    private Boolean available;
    private User owner;
    private Boolean isRequested;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
