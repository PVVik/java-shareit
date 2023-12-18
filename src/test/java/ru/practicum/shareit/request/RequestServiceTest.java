package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {
    private final RequestService requestService;
    private final UserService userService;

    private final UserDto andrew = new UserDto(
            1L,
            "Andrew",
            "Andrew@mail.ru");
    private final UserDto anna = new UserDto(
            2L,
            "Anna",
            "anna@ya.ru");
    private final RequestDto request = new RequestDto(
            21L,
            "I need a pillow to sleep on",
            andrew,
            LocalDateTime.of(2022, 10, 12, 21, 40, 0),
            null);

    @Test
    void create_shouldCreateRequest() {
        UserDto thisUser = userService.create(andrew);
        RequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2022, 10, 12, 21, 40, 0));

        assertThat(thisRequest.getDescription(), equalTo(request.getDescription()));
    }

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.create(999L, request, LocalDateTime.now()));
    }

    @Test
    void getRequestsByOwner_shouldReturnRequests() {
        UserDto thisUser = userService.create(andrew);
        RequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2022, 10, 12, 21, 40, 0));
        List<RequestDto> returnedRequest = requestService.getRequestsByOwner(thisUser.getId());

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getRequestsByOwner_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestsByOwner(999L));
    }

    @Test
    void getExistingRequests_shouldReturnExistingRequests() {
        UserDto thisUser = userService.create(andrew);
        UserDto thisAnna = userService.create(anna);
        RequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2022, 10, 12, 21, 40, 0));
        List<RequestDto> returnedRequest = requestService.getExistingRequests(thisAnna.getId(), 0, 10);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getExistingRequests_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getExistingRequests(999L, 0, 10));
    }

    @Test
    void getExistingRequests_shouldReturnExistingRequestsIfSizeIsNull() {
        UserDto thisUser = userService.create(andrew);
        UserDto thisAnna = userService.create(anna);
        RequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2022, 10, 12, 21, 40, 0));
        List<RequestDto> returnedRequest = requestService.getExistingRequests(thisAnna.getId(), 0, null);

        assertFalse(returnedRequest.isEmpty());
        assertTrue(returnedRequest.contains(thisRequest));
    }

    @Test
    void getRequestById_shouldReturnRequest() {
        UserDto thisUser = userService.create(andrew);
        RequestDto thisRequest = requestService.create(thisUser.getId(), request,
                LocalDateTime.of(2022, 10, 12, 21, 40, 0));
        RequestDto returnedRequest = requestService.getRequestById(thisUser.getId(), thisRequest.getId());

        assertEquals(thisRequest.getDescription(), returnedRequest.getDescription());
        assertEquals(thisRequest.getRequester(), returnedRequest.getRequester());
    }

    @Test
    void getRequestById_shouldThrowExceptionIfRequestIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestById(999L, 999L));
    }

    @Test
    void getRequestById_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestById(999L, 999L));
    }
}
