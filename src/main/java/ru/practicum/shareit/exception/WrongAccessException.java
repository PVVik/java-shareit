package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrongAccessException extends RuntimeException {
    public WrongAccessException(String message) {
        super(message);
        log.error(message);
    }
}
