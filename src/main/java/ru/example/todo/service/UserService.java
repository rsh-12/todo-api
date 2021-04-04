package ru.example.todo.service;

import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    String login(UserDto userDto);

    String register(User user);

    String refreshToken(String token);

    void deleteUser(Long userId);

    User getUser(String username);

}
