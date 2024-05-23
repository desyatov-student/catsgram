package ru.yandex.practicum.catsgram.exception;

import java.io.IOException;

public class ImageFileException extends RuntimeException {
    public ImageFileException(String message, IOException exception) {
        super(message, exception);
    }

    public ImageFileException(String message) {
        super(message);
    }
}
