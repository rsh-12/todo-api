package ru.example.todoapp.util;

import static ru.example.todoapp.domain.Role.ADMIN;

import java.util.function.Predicate;
import ru.example.todoapp.entity.User;

public final class Util {

    private Util() {
        throw new AssertionError();
    }

    public static Predicate<User> checkUserAccess(User candidate) {
        Predicate<User> predicate = user -> user.equals(candidate);
        return predicate.or(user -> user.getRoles().contains(ADMIN));
    }

    public static Predicate<User> authorizeUser(Long id) {
        Predicate<User> predicate = user -> user.getId().equals(id);
        return predicate.or(user -> user.getRoles().contains(ADMIN));
    }

}
