package ru.example.todoapp.util;

import ru.example.todoapp.entity.User;

import java.util.function.Predicate;

import static ru.example.todoapp.enums.Role.ADMIN;

public record Combinators() {

    public static Predicate<User> checkUserAccess(User candidate) {
        Predicate<User> predicate = user -> user.equals(candidate);
        return predicate.or(user -> user.getRoles().contains(ADMIN));
    }

}
