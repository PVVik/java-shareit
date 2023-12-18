package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestRepositoryTest {
    @Mock
    private RequestRepository requestRepository;
    private RequestService requestService;

    @Test
    void getById_shouldThrowExceptionIfWrongId() {
        requestService = new RequestServiceImpl(requestRepository, null, null);

        assertThrows(NullPointerException.class,
                () -> requestService.getRequestById(1L, 999L));
    }
}
