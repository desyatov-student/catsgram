package ru.yandex.practicum.catsgram.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Pagination {
    private SortOrder sortOrder;
    private Integer from;
    private Integer size;
}