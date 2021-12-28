package ru.example.todoapp.util;

import java.util.function.Predicate;
import ru.example.todoapp.entity.TodoSection;
import ru.example.todoapp.facade.AuthUserFacade;

public final class Util {

    private Util() {
        throw new AssertionError();
    }

    public static Predicate<TodoSection> ownerOrAdmin(AuthUserFacade currentUser) {
        return s -> s.getUserId().equals(currentUser.getId())
                || currentUser.containsRoleAdmin();
    }

}
