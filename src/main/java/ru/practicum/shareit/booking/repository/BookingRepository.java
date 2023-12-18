package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBooker(User booker, Pageable page);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          Status status, LocalDateTime end);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start,
                                                           LocalDateTime end, Pageable page);

    Page<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end, Pageable page);

    Page<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start, Pageable page);

    Page<Booking> findAllByBookerAndStatusEquals(User booker, Status status, Pageable page);

    Page<Booking> findAllByItemOwner(User owner, Pageable page);

    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start,
                                                              LocalDateTime end, Pageable page);

    Page<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime end, Pageable page);

    Page<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime start, Pageable page);

    Page<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Pageable page);

    Optional<Booking> findFirstByItemIdInAndStartLessThanEqualAndStatus(List<Long> idItems, LocalDateTime now,
                                                                        Status approved, Sort sort);

    Optional<Booking> findFirstByItemIdInAndStartAfterAndStatus(List<Long> idItems, LocalDateTime now,
                                                                Status approved, Sort sort);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime date);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime date);
}