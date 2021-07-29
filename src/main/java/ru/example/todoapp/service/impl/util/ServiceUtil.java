package ru.example.todoapp.service.impl.util;
/*
 * Date: 18.07.2021
 * Time: 12:33 AM
 * */

import org.springframework.data.domain.Sort;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.Role;
import ru.example.todoapp.exception.CustomException;

public record ServiceUtil() {

    public static Sort.Direction getSortDirection(String sort) {
        if (sort.contains(",asc")) return Sort.Direction.ASC;
        return Sort.Direction.DESC;
    }

    public static String getSortAsString(String sort) {
        if (sort.contains(",")) return sort.split(",")[0];
        return sort;
    }

    public static void validateUser(User principal, User owner) {
        boolean isValid = (owner != null && owner.equals(principal)) || principal.getRoles().contains(Role.ADMIN);
        if (!isValid) {
            throw CustomException.forbidden("Not enough permissions");
        }
    }

}