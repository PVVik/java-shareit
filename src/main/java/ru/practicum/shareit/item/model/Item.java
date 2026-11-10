package ru.practicum.shareit.item.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
public class Item {

    private long id;

    private String name;

    private String description;

    private boolean available;

    private Long ownerId;
}
