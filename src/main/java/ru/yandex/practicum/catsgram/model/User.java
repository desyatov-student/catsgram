package ru.yandex.practicum.catsgram.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;

@Value
@EqualsAndHashCode(of = {"email"})
@Builder(toBuilder = true)
public class User {
    Long id;
    String username;
    String email;
    String password;
    Instant registrationDate;
}
