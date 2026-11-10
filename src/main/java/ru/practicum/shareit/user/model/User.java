package ru.practicum.shareit.user.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = {"id", "email"})
@ToString
public class User {

    private long id;

    private String email;

    private String name;
}
