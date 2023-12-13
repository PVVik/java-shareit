package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemMapper mapper;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemShortDto item) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        Item thisItem = mapper.toItem(item);
        thisItem.setOwner(user);
        return mapper.toItemDto(itemRepository.save(thisItem));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemShortDto item) {
        Item thisItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Товар не найден"));
        if (!thisItem.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Пользователь не является владельцем товара");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            thisItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            thisItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            thisItem.setAvailable(item.getAvailable());
        }
        return mapper.toItemDto(thisItem);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item thisItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Товар не найден"));
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(mapper.toItemDto(thisItem));
        if (thisItem.getOwner().getId().equals(userId)) {
            List<Long> items = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
            getBookingsByItem(itemDtoList, items);
        }
        ItemDto item = itemDtoList.get(0);
        item.setComments(commentRepository.getAllByItemId(itemId).stream().map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return item;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentShortDto comment) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Товар не найден"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new AccessException("Ошибка доступа");
        }
        Comment thisComment = commentMapper.toComment(comment);
        thisComment.setItem(item);
        thisComment.setAuthor(user);
        thisComment.setCreated(LocalDateTime.now());
        commentRepository.save(thisComment);
        return commentMapper.toCommentDto(thisComment);
    }

    @Transactional
    @Override
    public void deleteById(Long userId, Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> getByUserId(Long id) {
        List<Item> items = itemRepository.findItemByOwnerId(id);
        List<ItemDto> itemDtoList = items.stream().map(mapper::toItemDto).collect(Collectors.toList());
        List<Long> itemsIds = itemDtoList.stream().map(ItemDto::getId).collect(Collectors.toList());
        getBookingsByItem(itemDtoList, itemsIds);
        Map<Long, List<CommentDto>> comments = commentRepository
                .getAllByItemIdIn(itemsIds, Sort.by(DESC, "created"))
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getId));
        itemDtoList.forEach(i -> i.setComments(comments.get(i.getId())));
        return itemDtoList;
    }

    @Override
    public List<ItemShortDto> search(String text) {
        Boolean available = true;
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, available)
                .stream().map(mapper::toItemShortDto).collect(Collectors.toList());
    }

    private void getBookingsByItem(List<ItemDto> itemDtoList, List<Long> items) {
        Map<Long, BookingForItemDto> lastBookings = bookingRepository.findFirstByItemIdInAndStartLessThanEqualAndStatus(
                        items, LocalDateTime.now(), Status.APPROVED, Sort.by(DESC, "start"))
                .stream()
                .map(bookingMapper::bookingForItemDto)
                .collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        Map<Long, BookingForItemDto> nextBookings = bookingRepository.findFirstByItemIdInAndStartAfterAndStatus(
                        items, LocalDateTime.now(), Status.APPROVED, Sort.by(Sort.Direction.ASC, "start"))
                .stream()
                .map(bookingMapper::bookingForItemDto)
                .collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        for (ItemDto item: itemDtoList) {
            item.setLastBooking(lastBookings.get(item.getId()));
            item.setNextBooking(nextBookings.get(item.getId()));
        }
    }
}