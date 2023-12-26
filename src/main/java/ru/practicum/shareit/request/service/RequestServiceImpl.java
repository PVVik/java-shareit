package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    public RequestDto create(Long userId, RequestDto request, LocalDateTime created) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        Request thisRequest = mapper.toRequest(request, userId, created);
        return mapper.toRequestDto(requestRepository.save(thisRequest));
    }

    @Override
    public List<RequestDto> getRequestsByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        return requestRepository.findAllByRequesterId(userId, Sort.by(DESC, "created"))
                .stream().map(mapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getExistingRequests(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        List<RequestDto> requests = new ArrayList<>();
        Pageable pageable;
        Page<Request> page;
        Pagination pagination = new Pagination(from, size);
        Sort sort = Sort.by(DESC, "created");
        if (size == null) {
            List<Request> requestsList = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
            requests.addAll(requestsList.stream().skip(from).map(mapper::toRequestDto).collect(Collectors.toList()));
        } else {
            for (int i = pagination.getIndex(); i < pagination.getTotalPages(); i++) {
                pageable = PageRequest.of(i, pagination.getPageSize(), sort);
                page = requestRepository.findAllByRequesterIdNot(userId, pageable);
                requests.addAll(page.stream().map(mapper::toRequestDto).collect(Collectors.toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            requests = requests.stream().limit(size).collect(Collectors.toList());
        }
        return requests;
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));
        return mapper.toRequestDto(request);
    }
}
