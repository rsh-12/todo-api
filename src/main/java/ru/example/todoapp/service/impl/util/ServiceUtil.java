package ru.example.todoapp.service.impl.util;
/*
 * Date: 18.07.2021
 * Time: 12:33 AM
 * */

import ru.example.todoapp.dto.UserDto;
import ru.example.todoapp.entity.User;
import ru.example.todoapp.enums.Role;
import ru.example.todoapp.exception.CustomException;

public record ServiceUtil() {

    public static void validateUser(User principal, User owner) {
        boolean isValid = (owner != null && owner.equals(principal)) || principal.getRoles().contains(Role.ADMIN);
        if (!isValid) {
            throw CustomException.forbidden("Not enough permissions");
        }
    }

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getUsername(), user.getCreatedAt());
    }

}
