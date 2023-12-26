package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final UserDto matthew = new UserDto(
            1L,
            "Matthew",
            "matthew@mail.ru");
    private final UserDto andrew = new UserDto(
            2L,
            "Andrew",
            "andrew@ya.ru");
    private final ItemShortDto toy = new ItemShortDto(
            5L,
            "A toy",
            "An old ugly toy",
            true,
            null);

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(999L, toy));
    }

    @Test
    void update_shouldThrowExceptionIfItemIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(matthew.getId(), 999L, null));
    }

    @Test
    void update_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(999L, toy.getId(), toy));
    }

    @Test
    void update_shouldThrowExceptionIfNotUserUpdating() {
        UserDto thisUser = userService.create(matthew);
        UserDto thisAndrew = userService.create(andrew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(thisAndrew.getId(), thisItem.getId(), new ItemShortDto(thisItem.getId(),
                        thisItem.getName(), thisItem.getDescription(),
                        thisItem.getAvailable(), thisItem.getRequestId())));
    }

    @Test
    void update_shouldUpdateIfItemNameIsNull() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        thisItem.setName(null);
        ItemDto updatedItem = itemService.update(thisUser.getId(), thisItem.getId(), new ItemShortDto(thisItem.getId(),
                thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()));

        assertEquals(thisItem.getDescription(), updatedItem.getDescription());
        assertEquals(thisItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void update_shouldUpdateIfItemDescriptionIsNull() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        thisItem.setDescription(null);
        ItemDto updatedItem = itemService.update(thisUser.getId(), thisItem.getId(), new ItemShortDto(thisItem.getId(),
                thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()));

        assertEquals(thisItem.getName(), updatedItem.getName());
        assertEquals(thisItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void update_shouldUpdateIfItemAvailableIsNull() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        thisItem.setAvailable(null);
        ItemDto updatedItem = itemService.update(thisUser.getId(), thisItem.getId(), new ItemShortDto(thisItem.getId(),
                thisItem.getName(), thisItem.getDescription(), thisItem.getAvailable(), thisItem.getRequestId()));

        assertEquals(thisItem.getName(), updatedItem.getName());
        assertEquals(thisItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void getById_shouldThrowExceptionIfIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> itemService.getById(999L, 999L));
    }

    @Test
    void getById_shouldReturnItemIfOwnerRequesting() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        ItemDto returnedItem = itemService.getById(thisUser.getId(), thisItem.getId());

        assertEquals(returnedItem.getOwner().getEmail(), thisUser.getEmail());
        assertEquals(thisItem.getName(), returnedItem.getName());
    }

    @Test
    void getById_shouldReturnItemIfUserRequesting() {
        UserDto thisUser = userService.create(matthew);
        UserDto thisAndrew = userService.create(andrew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        ItemDto returnedItem = itemService.getById(thisAndrew.getId(), thisItem.getId());

        assertEquals(returnedItem.getDescription(), thisItem.getDescription());
        assertEquals(returnedItem.getName(), thisItem.getName());
    }

    @Test
    void deleteById_shouldDeleteById() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);
        itemService.deleteById(thisUser.getId(), thisItem.getId());

        assertEquals(0, itemService.getByUserId(thisUser.getId(), 0, 10).size());
    }

    @Test
    void deleteById_shouldThrowExceptionIfUserIsNotOwningAnItem() {
        UserDto thisUser = userService.create(matthew);
        UserDto thisAndrew = userService.create(andrew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);

        assertThrows(AccessException.class,
                () -> itemService.deleteById(thisAndrew.getId(), thisItem.getId()));
    }

    @Test
    void deleteById_shouldThrowExceptionIfItemIdIsIncorrect() {
        UserDto thisUser = userService.create(matthew);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.deleteById(thisUser.getId(), 999L));
    }

    @Test
    void deleteById_shouldThrowExceptionIfUserIdIsIncorrect() {
        UserDto thisUser = userService.create(matthew);
        ItemDto thisItem = itemService.create(thisUser.getId(), toy);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.deleteById(999L, thisItem.getId()));
    }

    @Test
    void getByUserId_shouldReturnByUserIdIfLimitIsNull() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);
        List<ItemDto> items = itemService.getByUserId(thisUser.getId(), 0, null);

        assertFalse(items.isEmpty());
    }

    @Test
    void getByUserId_shouldReturnByUserId() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);
        List<ItemDto> items = itemService.getByUserId(thisUser.getId(), 0, 10);

        assertFalse(items.isEmpty());
    }

    @Test
    void search_shouldReturnItemIfLimitIsNull() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);
        List<ItemDto> items = itemService.search("toy", 0, null);

        assertFalse(items.isEmpty());
    }

    @Test
    void search_shouldReturnItem() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);
        List<ItemDto> items = itemService.search("toy", 0, 10);

        assertFalse(items.isEmpty());
    }

    @Test
    void search_shouldThrowExceptionIfFromLessThanZero() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);

        assertThrows(IllegalArgumentException.class,
                () -> itemService.search("toy", -1, null));
    }

    @Test
    void search_shouldThrowExceptionIfSizeIsZero() {
        UserDto thisUser = userService.create(matthew);
        itemService.create(thisUser.getId(), toy);

        assertThrows(ValidationException.class,
                () -> itemService.search("toy", 0, 0));
    }
}
