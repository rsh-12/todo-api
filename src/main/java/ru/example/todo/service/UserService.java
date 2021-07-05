package ru.example.todo.service;

import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;

import java.util.Map;

public interface UserService {

    /**
     * Authenticates (not completed) the user and returns access and refresh tokens.
     *
     * @param user contains username (email) and password.
     * @return access, refresh tokens, token type and expiration time in milliseconds.
     * @throws CustomException if the user by username not found in the database.
     */
    Map<String, String> login(User user, String ip);

    String register(User user);

    Map<String, String> generateNewTokens(String refreshToken, String ip);

    void deleteUserById(Long userId);

    User findUserById(Long userId);

    void updatePassword(String email, String password);

    boolean existsByUsername(String email);
}
