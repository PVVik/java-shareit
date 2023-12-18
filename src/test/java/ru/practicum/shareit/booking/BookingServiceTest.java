package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private final BookingMapper mapper = new BookingMapper();
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, mapper);
    }

    private final User arina = new User(
            1L,
            "Arina",
            "arina@mail.ru");
    private final User ilya = new User(
            2L,
            "Ilya",
            "iliya@ya.ru");
    private final User ivan = new User(
            3L,
            "Ivan",
            "ivan@mail.ru");
    private final Item flour = new Item(
            1L,
            "Baking flour",
            "In addition, I'll give you a baking powder",
            true,
            arina,
            null);
    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2023, 12, 12, 12, 12, 0),
            LocalDateTime.of(2024, 1, 12, 12, 12, 0),
            flour, ilya, Status.WAITING);

    @Test
    void create_shouldThrowExceptionIfUserIdIsIncorrect() {
        when(userRepository.findById(anyLong()))
                .thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(999L,
                        new BookingShortDto(booking.getStart(), booking.getEnd(), booking.getItem().getId())));
    }

    @Test
    void create_shouldThrowExceptionIfItemIdIsIncorrect() {
        BookingShortDto booking = new BookingShortDto(
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                999L);

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(ilya.getId(), booking));
    }

    @Test
    void create_shouldThrowExceptionIfAvailableIsFalse() {
        Item flour = new Item(
                1L,
                "Baking flour",
                "In addition, I'll give you a baking powder",
                false);
        BookingShortDto booking = new BookingShortDto(
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour.getId());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(ilya.getId(), booking));
    }

    @Test
    void create_shouldThrowExceptionIfOwnerIsBooking() {
        Item thisFlour = new Item(
                1L,
                "Baking flour",
                "In addition, I'll give you a baking powder",
                false,
                arina,
                null);
        BookingShortDto thisBooking = new BookingShortDto(
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                thisFlour.getId());

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(arina.getId(), thisBooking));
    }

    @Test
    void create_shouldThrowExceptionIfEndIsBeforeStart() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(flour));

        assertThrows(ValidationException.class,
                () -> bookingService.create(ilya.getId(),
                        new BookingShortDto(
                                LocalDateTime.of(2023, 10, 10, 10, 10, 0),
                                LocalDateTime.of(2022, 10, 10, 10, 10, 0),
                                flour.getId())));
    }

    @Test
    void updateStatus_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateStatus(arina.getId(), 999L, true));
    }

    @Test
    void updateStatus_shouldThrowExceptionIfNotOwnerUpdatingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateStatus(ilya.getId(), 1L, true));
    }

    @Test
    void updateStatus_shouldThrowExceptionIfStatusIsRejected() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(thisBooking));

        assertThrows(AccessException.class,
                () -> bookingService.updateStatus(arina.getId(), thisBooking.getId(), true));
    }

    @Test
    void getById_shouldReturnBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.getById(booking.getId(), ilya.getId());

        assertThat(returnedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void getById_shouldThrowExceptionIfBookingIdIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getById(999L, 999L));
    }

    @Test
    void getById_shouldThrowExceptionIfNeitherBookerOrOwnerIsGettingBooking() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getById(1L, ivan.getId()));
    }


    @Test
    void getBookingsByOwner_shouldReturnBookingsIfStateIsCurrent() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(arina.getId(),
                State.CURRENT, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfStateIsPast() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2022, 12, 12, 12, 12, 0),
                LocalDateTime.of(2023, 1, 12, 12, 12, 0),
                flour, ilya, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(arina.getId(),
                State.PAST, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfStateIsFuture() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(arina.getId(),
                State.FUTURE, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfStateIsWaiting() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(arina.getId(),
                State.WAITING, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldReturnBookingsIfStateIsRejected() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        List<BookingDto> bookings = bookingService.getBookingsByOwner(arina.getId(),
                State.REJECTED, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_shouldThrowExceptionIfStateIsUnsupported() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(arina));

        assertThrows(AccessException.class,
                () -> bookingService.getBookingsByOwner(arina.getId(),
                        State.UNSUPPORTED_STATUS, 0, 10));
    }

    @Test
    void getBookingsByUser_shouldThrowExceptionIfUseridIsIncorrect() {
        assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsByUser(999L, State.ALL, 0, 10));
    }

    @Test
    void getBookingsByUser_shouldReturnBookingIfSizeIsNull() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.ALL, 0, null);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsAll() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.ALL, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsCurrent() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 10, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.CURRENT, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsPast() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2022, 10, 12, 12, 12, 0),
                LocalDateTime.of(2023, 1, 12, 12, 12, 0),
                flour, ilya, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByBookerAndEndBefore(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.PAST, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsFuture() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.APPROVED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByBookerAndStartAfter(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.FUTURE, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsWaiting() {
        Page<Booking> pages = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.WAITING, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsIfStateIsRejected() {
        Booking thisBooking = new Booking(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 12, 0),
                LocalDateTime.of(2024, 1, 12, 12, 12, 0),
                flour, ilya, Status.REJECTED);
        Page<Booking> pages = new PageImpl<>(List.of(thisBooking));
        when(bookingRepository.findAllByBookerAndStatusEquals(any(), any(), any()))
                .thenReturn(pages);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        List<BookingDto> bookings = bookingService.getBookingsByUser(ilya.getId(),
                State.REJECTED, 0, 10);

        assertFalse(bookings.isEmpty());
    }

    @Test
    void getBookingsByUser_shouldThrowExceptionIfStateIsUnsupported() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ilya));

        assertThrows(AccessException.class,
                () -> bookingService.getBookingsByOwner(ilya.getId(),
                        State.UNSUPPORTED_STATUS, 0, 10));
    }
}
