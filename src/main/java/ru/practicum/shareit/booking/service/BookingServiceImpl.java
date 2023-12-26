package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingShortDto booking) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(
                () -> new ObjectNotFoundException("Товара не существует"));
        if (item.getAvailable().equals(false)) {
            throw new AccessException("Товар недоступен для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Ошибка доступа");
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
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
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
    public List<BookingDto> getBookingsByOwner(Long userId, State state, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size, Sort.by(DESC, "start"));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        Page<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByItemOwner(user, page);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), page);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, page);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, page);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, State state, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        List<BookingDto> list = new ArrayList<>();
        Page<Booking> bookingList;
        Sort sort = Sort.by(DESC, "start");
        Pageable page;
        Pagination pagination = new Pagination(from, size);
        if (size == null) {
            page = PageRequest.of(pagination.getIndex(), pagination.getPageSize(), sort);
            do {
                bookingList = getPages(state, user, page);
                list.addAll(bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList()));
                page = page.next();
            } while (bookingList.hasNext());
        } else {
            for (int i = pagination.getIndex(); i < pagination.getTotalPages(); i++) {
                page = PageRequest.of(i, pagination.getPageSize(), sort);
                bookingList = getPages(state, user, page);
                list.addAll(bookingList.stream().map(mapper::toBookingDto).collect(Collectors.toList()));
                if (!bookingList.hasNext()) {
                    break;
                }
            }
            list = list.stream().limit(size).collect(Collectors.toList());
        }
        return list;
    }

    private Page<Booking> getPages(State state, User user, Pageable pageable) {
        Page<Booking> bookingList;
        switch (state) {
            case ALL:
                bookingList = bookingRepository.findAllByBooker(user, pageable);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerAndEndBefore(user,
                        LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, pageable);
                break;
            default:
                throw new AccessException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingList;
    }

    @Override
    public BookingForItemDto getLastBooking(Long itemId) {
        return mapper.bookingForItemDto(bookingRepository
                .findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
    }

    @Override
    public BookingForItemDto getNextBooking(Long itemId) {
        return mapper.bookingForItemDto(bookingRepository
                .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now()));
    }
}