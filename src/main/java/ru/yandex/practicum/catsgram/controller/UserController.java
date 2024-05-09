package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        boolean exists = users.values().stream().map(User::getEmail).anyMatch(email -> email.equals(user.getEmail()));
        if (exists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        // формируем дополнительные данные
        final User newUser = user.toBuilder()
                .id(getNextId())
                .registrationDate(Instant.now())
                .build();
        // сохраняем нового пользователя в памяти приложения
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            String newEmail = newUser.getEmail();
            User.UserBuilder userBuilder = oldUser.toBuilder();
            if (newEmail != null && !newEmail.isBlank()) {
                boolean newEmailExists = users.values().stream()
                        .anyMatch(user -> {
                            if (user.getId().equals(oldUser.getId())) {
                                return false;
                            }
                            return user.getEmail().equals(newEmail);
                        });
                if (newEmailExists) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
                userBuilder.email(newEmail);
            }

            if (newUser.getUsername() != null && !newUser.getUsername().isBlank()) {
                userBuilder.username(newUser.getUsername());
            }

            if (newUser.getPassword() != null && !newUser.getPassword().isBlank()) {
                userBuilder.password(newUser.getPassword());
            }

            User finalUser = userBuilder.build();
            users.put(oldUser.getId(), finalUser);
            return finalUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}