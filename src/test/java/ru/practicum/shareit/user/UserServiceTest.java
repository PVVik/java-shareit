package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private final UserDto user = new UserDto(1L, "Alexander", "alex@ya.ru");

    @Test
    void update_shouldThrowExceptionIfUserNotFound() {
        assertThrows(ObjectNotFoundException.class,
                () -> userService.update(999L, new UserDto(999L, "Someone", "some@mail.ru")));
    }

    @Test
    void update_shouldUpdateUser() {
        UserDto thisUser = userService.create(user);
        thisUser.setName("Alex");
        UserDto updatedUser = userService.update(thisUser.getId(), thisUser);

        assertEquals(updatedUser.getEmail(), thisUser.getEmail());
    }

    @Test
    void update_shouldUpdateIfNameIsNull() {
        UserDto thisUser = userService.create(user);
        thisUser.setName(null);
        thisUser.setEmail("alex@ya.ru");
        UserDto updatedUser = userService.update(thisUser.getId(), thisUser);

        assertEquals(updatedUser.getEmail(), thisUser.getEmail());
    }

    @Test
    void update_shouldUpdateIfEmailIsNull() {
        UserDto thisUser = userService.create(user);
        thisUser.setName("Alex");
        thisUser.setEmail(null);
        UserDto updatedUser = userService.update(thisUser.getId(), thisUser);

        assertEquals(updatedUser.getName(), thisUser.getName());
    }

    @Test
    void deleteById_shouldDeleteById() {
        UserDto thisUser = userService.create(user);
        userService.deleteById(thisUser.getId());

        assertTrue(userService.getUsers().isEmpty());
    }

    @Test
    void getUsers_shouldReturnListOfUsers() {
        UserDto first = userService.create(new UserDto(3L, "Anna", "anna@mail.ru"));
        UserDto second = userService.create(new UserDto(5L, "Matthew", "matthew@mail.ru"));

        assertEquals(2, userService.getUsers().size());
        assertTrue(userService.getUsers().contains(first));
        assertTrue(userService.getUsers().contains(second));
    }
}
