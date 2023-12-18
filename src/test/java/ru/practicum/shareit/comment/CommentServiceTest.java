package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentServiceTest {
    private final CommentService commentService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private final UserDto maria = new UserDto(
            1L,
            "Maria",
            "maria@mail.ru");
    private final UserDto andrew = new UserDto(
            2L,
            "Andrew",
            "andrew@mail.ru");
    private final ItemShortDto item = new ItemShortDto(
            2L,
            "Lollipop",
            "A yummy one",
            true,
            null);
    private final CommentShortDto comment = new CommentShortDto(
            1L,
            "I ate your lollipop",
            "Andrew",
            LocalDateTime.of(2020, 5, 13, 1, 0, 0));

    @Test
    void getCommentsByItemId_shouldReturnComment() {
        UserDto thisUser = userService.create(maria);
        UserDto thisAndrew = userService.create(andrew);
        ItemDto thisItem = itemService.create(thisUser.getId(), item);
        BookingShortDto bookingDto = new BookingShortDto(
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3),
                thisItem.getId());
        BookingDto thisBooking = bookingService.create(thisAndrew.getId(), bookingDto);
        bookingService.updateStatus(thisUser.getId(), thisBooking.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        itemService.createComment(thisItem.getId(), thisAndrew.getId(), comment);
        List<CommentDto> thisComment = commentService.getCommentsByItemId(thisItem.getId());

        assertFalse(thisComment.isEmpty());
    }

    @Test
    void createComment_shouldThrowExceptionIfUserIdIsIncorrect() {
        UserDto thisUser = userService.create(maria);
        ItemDto thisItem = itemService.create(thisUser.getId(), item);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.createComment(thisItem.getId(), 999L, comment));
    }

    @Test
    void createComment_shouldThrowExceptionIfItemIdIsIncorrect() {
        UserDto thisUser = userService.create(maria);

        assertThrows(ObjectNotFoundException.class,
                () -> itemService.createComment(999L, thisUser.getId(), comment));
    }

    @Test
    void createComment_shouldThrowExceptionIfUserDidntBookItem() {
        UserDto thisUser = userService.create(maria);
        UserDto thisAndrew = userService.create(andrew);
        ItemDto thisItem = itemService.create(thisUser.getId(), item);

        assertThrows(AccessException.class,
                () -> itemService.createComment(thisItem.getId(), thisAndrew.getId(), comment));
    }
}
