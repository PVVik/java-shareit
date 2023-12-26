package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByItemId(Long itemId);
}
