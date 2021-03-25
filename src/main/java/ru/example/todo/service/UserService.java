package ru.example.todo.service;

import ru.example.todo.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {

    String login(String username, String password);

    Map<String, String> register(String username, String password);

    Map<String, String> refreshToken(String token);

    String deleteUser(String username);

    User getUser(String username);

    User whoAmI(HttpServletRequest request);

}
