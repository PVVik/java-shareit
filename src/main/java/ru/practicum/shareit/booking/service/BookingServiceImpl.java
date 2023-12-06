package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;
    private static final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingShortDto booking) {
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(
                () -> new ObjectNotFoundException("Товара не существует"));
        if (item.getAvailable().equals(false)) {
            throw new AccessException("Товар недоступен для бронирования");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Не найдено");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException("Время начала не может быть равно времени окончания заявки");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd() == booking.getStart()) {
            throw new ValidationException("Ошибка валидации времени");
        }
        Booking thisBooking = mapper.toBooking(booking);
        thisBooking.setItem(item);
        thisBooking.setBooker(user);
        thisBooking.setStatus(Status.WAITING);
        Booking savedBooking = bookingRepository.save(thisBooking);
        return mapper.toBookingDto(savedBooking);
    }

    @Transactional
    @Override
    public BookingDto updateStatus(Long bookingId, Long userId, Boolean approved) {
        Booking thisBooking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException("Бронирование не найдено"));
        if (!userId.equals(thisBooking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("Не найдено");
        }
        if (!thisBooking.getStatus().equals(Status.WAITING)) {
            throw new AccessException("Нельзя изменить статус");
        }
        thisBooking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return mapper.toBookingDto(thisBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ObjectNotFoundException("Бронирование не найдено"));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("Неверный запрос");
        }
        return mapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBooker(user, sort);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }
}
