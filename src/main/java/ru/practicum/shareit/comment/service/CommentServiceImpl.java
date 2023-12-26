package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final CommentMapper mapper;

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return repository.findAllByItemId(itemId, Sort.by(DESC, "created"))
                .stream()
                .map(mapper::toCommentDto)
                .collect(toList());
    }
}
