package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Component;

@Component
public class IdentifierGenerator {

    public static final Long INITIAL_IDENTIFIER = 1L;
    private Long currentId;

    public IdentifierGenerator() {
        this.currentId = INITIAL_IDENTIFIER;
    }

    public IdentifierGenerator(Long initialId) {
        this.currentId = initialId;
    }

    public Long getNextId() {
        Long nextId = currentId;
        currentId++;
        return nextId;
    }
}
