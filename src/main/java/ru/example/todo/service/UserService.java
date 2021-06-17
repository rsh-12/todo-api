package ru.example.todo.service;

import ru.example.todo.dto.UserDto;
import ru.example.todo.entity.User;
import ru.example.todo.exception.CustomException;

public interface UserService {

    /**
     * Authenticates (not completed) the user and returns access and refresh tokens.
     *
     * @param userDto contains username (email) and password.
     * @return access, refresh tokens, token type and expiration time in milliseconds.
     * @throws CustomException if the user by username not found in the database.
     */
    String login(UserDto userDto);

    String register(User user);

    String generateNewTokens(String refreshToken);

    void deleteUserById(Long userId);

    User findUserByUsername(String username);

    User findUserById(Long userId);

    void updatePassword(User user, String password);

    void updatePassword(String email, String password);

    boolean existsByUsername(String email);
}
