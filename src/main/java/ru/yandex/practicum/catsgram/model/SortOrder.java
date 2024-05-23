package ru.yandex.practicum.catsgram.model;

public enum SortOrder {
    ASCENDING, DESCENDING;

    // Преобразует строку в элемент перечисления
    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "ascending", "asc" -> ASCENDING;
            case "descending", "desc" -> DESCENDING;
            default -> null;
        };
    }

    public boolean isAscending() {
        return this == SortOrder.ASCENDING;
    }
}
